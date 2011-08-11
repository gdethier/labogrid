/*
 * #%L
 * LaBoGrid
 * %%
 * Copyright (C) 2011 LaBoGrid Team
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package laboGrid.impl.common.simulation.algorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.regex.Pattern;

import laboGrid.CallBackNotImplemented;
import laboGrid.configuration.middleware.FaultToleranceConfiguration;
import laboGrid.impl.common.simulation.LBSimulation;
import laboGrid.impl.common.simulation.SimulationParameters;
import laboGrid.impl.common.simulation.algorithm.LBState.ContentType;
import laboGrid.ioClients.OutputClient;
import laboGrid.ioClients.OutputClientCB;
import laboGrid.ioClients.OutputClientCallBack;
import laboGrid.lb.LBException;
import laboGrid.lb.SubLattice;
import laboGrid.lb.lattice.Lattice;
import laboGrid.lb.lattice.LatticeDescriptor;
import laboGrid.lb.solid.Solid;
import laboGrid.procChain.ProcessingChain;
import laboGrid.procChain.ProcessingChainElement;

import dimawo.middleware.commonEvents.BrokenDA;
import dimawo.middleware.communication.CommunicatorInterface;
import dimawo.middleware.communication.Message;
import dimawo.middleware.communication.MessageHandler;
import dimawo.middleware.communication.outputStream.MOSAccessorInterface;
import dimawo.middleware.communication.outputStream.MOSCallBack;
import dimawo.middleware.distributedAgent.DAId;
import dimawo.middleware.fileSystem.DFSException;
import dimawo.middleware.fileSystem.FileSystemCallBack;
import dimawo.middleware.fileSystem.ReplicateFileResult;
import dimawo.middleware.fileSystem.fileDB.FileDescriptor.FileMode;




public class LBSimThread
implements Runnable, FileSystemCallBack, MOSAccessorInterface, OutputClientCallBack, MessageHandler, MOSCallBack {


	////////////////////
	// Static methods //
	////////////////////
	
	public static Pattern getStatePattern() {
		return Pattern.compile(".*\\.state");
	}
	
	public static Pattern getAllPattern() {
		return Pattern.compile(".*\\.state|.*\\.solid");
	}

	public static Pattern getStatePattern(int state) {
		return Pattern.compile(".*_"+state+"\\.state");
	}

	public static String getStateFileUID(int subId, int iteration) {
		return subId+"_"+iteration+".state";
	}


	/////////////
	// Members //
	/////////////

	private LBSimulation manager;
	private DAId thisDaId;
	private Thread algThread;
	private SimulationParameters simParam;

	// Communications structures
	/**
	 * Sub-Lattice to neighbor map
	 */
	private Map<Integer, MOSAccessorInterface> sub2Neighbor;
	/**
	 * Sub-lattice to link map
	 */
	private Map<Integer, List<Integer>> subL2Link;
	private LinkedBlockingQueue<BorderDataInfo>[] receivedBorders;

	// LB data
	private int simNumber;
	private int version;
	private SubLattice subLattice;
	private int fromIteration, toIteration;
	private int currentIteration;
	private long simDuration;

	private Solid solid;
	private Lattice fluid;
	private ProcessingChain operationChain;

	private int backupRate;
	private boolean keepLastState;
	private OutputClient outputClient;
	
	private Semaphore waitBackupSem, waitFinalDump, outputClientSync;
	private int fileNum;
	
	private boolean killFlag;


	//////////////////
	// Constructors //
	//////////////////

	/**
	 * Constructor for LBSimulation.
	 */
	public LBSimThread(LBSimulation parent,
			int subId,
			SimulationParameters simParam,
			File file,
			ContentType ct) throws LBException, IOException {

		manager = parent;
		thisDaId = manager.getDistributedAgent().getDaId();
		this.simParam = simParam;
		
		initDataStructures(subId, simParam);

		if(simParam.getStartingIteration() == 0) {

			restoreSolidFromFile(file);
			this.fluid = simParam.getLatticeInstance().clone();
			fluid.setSize(solid.getSize());
			fluid.setEquilibrium();

		} else {

			restoreStateFromFile(file, ct);

		}

		initReceptionBuffers();
	
	}
	
	/**
	 * Constructor for null thread.
	 */
	public LBSimThread(LBSimulation parent,
			SimulationParameters simParam) throws LBException, IOException {
		manager = parent;
		thisDaId = manager.getDistributedAgent().getDaId();
		this.simParam = simParam;
		
		initDataStructures(-1, simParam);
	}
	
	/**
	 * Constructor for LBBenchmark.
	 * @throws LBException 
	 */
	public LBSimThread(
			Lattice lattice,
			Solid solid,
			ProcessingChain pc,
			int iterations) throws LBException {

		manager = new LBSimulation();
		thisDaId = new DAId("", 0, 0);

		this.fluid = lattice;
		this.solid = solid;
		this.operationChain = pc;

		this.fromIteration = 0;
		this.toIteration = iterations;

		initDataStructuresForBenchmark();
		initReceptionBuffers();
	}


	////////////////////
	// Public methods //
	////////////////////

	public void benchmark() throws LBException, InterruptedException {

		initProcessingChainElements(operationChain);
		for(int i = fromIteration; i < toIteration; ++i) {
			
			// Apply operators to lattice.
			Iterator<ProcessingChainElement> operationChainIterator =
				operationChain.iterator();

			while(operationChainIterator.hasNext()) {

				ProcessingChainElement currentOperator = operationChainIterator.next();
				currentOperator.apply();

			}

//			algPrintMessage("Benchmark iteration "+currentIteration+" done.");

		}

	}
	
	public int getSimulationNumber() {

		return simNumber;

	}
	
	public void notifyBackup() {
		
		waitBackupSem.release();

	}

	public void join() throws InterruptedException {
		if(algThread != null)
			algThread.join();
	}
	
	public synchronized void kill() {
		if(! killFlag) {
			killFlag = true;
			
			for(int i = 0; i < receivedBorders.length; ++i) {
				if(receivedBorders[i] != null)
					try {
						receivedBorders[i].put(new BorderDataInfo());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
			
			if(waitBackupSem != null)
				waitBackupSem.release();
			
			if(outputClientSync != null)
				outputClientSync.release();
			
			if(awaitedSent != null)
				awaitedSent.release(numOfAwaitedSent);
			
			if(algThread != null)
				algThread.interrupt();
		}
	}
	
	public synchronized void start() {
		if(subLattice == null)
			algThread = new Thread(this, "NullSimThread");
		else
			algThread = new Thread(this, "SimThread "+subLattice.getId());
		algThread.start();
	}
	
	public int getCurrentIteration() {
		
		return currentIteration;
		
	}

	public void addConsumer(int subId, MOSAccessorInterface n) throws LBException {

		if(subId < 0) {

			throw new LBException("The neighbor id must be positive.");

		}

		MOSAccessorInterface old = sub2Neighbor.put(subId, n);
		assert old == null || old == n;

	}

	public int getNodeId() {
		return subLattice.getId();
	}

	public Lattice getFluid() {
		return fluid;
	}

	public void disposeData() {

		solid = null;
		fluid = null;
		for(int i = 0; i < receivedBorders.length; ++i) {
			receivedBorders[i].clear();
		}

	}

	public Solid getSolid() {
		return solid;
	}

	public SubLattice getSubLattice() {
		return subLattice;
	}
	
	public int getVersion() {
		return version;
	}

	@Override
	public void writeBlockingMessage(Message m) throws InterruptedException {
		LBData ld = (LBData) m;
		if(ld.getVersion() == this.version) {
			BorderDataInfo inf = ld.getBorderDataInfo();

			LinkedBlockingQueue<BorderDataInfo> q = receivedBorders[inf.getLink()];
				assert q != null : "No queue for vel.: "+inf.getLink()+" for subL "+subLattice.getId();

			q.put(inf);
		} else {
			printMessage("WARNING: Ignored obsolete data.");
		}
	}

	public Map<Integer, List<Integer>> getSubL2Link() {
		return subL2Link;
	}

	public Map<Integer, MOSAccessorInterface> getNeighboringConsumers() {
		return sub2Neighbor;
	}

//	public boolean borderAvailable(int i) {
//		return !receivedBorders[i].isEmpty();
//	}

	public BorderDataInfo getReceivedBorder(int i) throws InterruptedException {
		return receivedBorders[i].take();
	}

	public int getBordersCount() {
		return fluid.getLatticeDescriptor().getVelocitiesCount();
	}

//	public int getBordersToReceive() {
//		return fluid.getLatticeDescriptor().getInBorderCount();
//	}

	public File generateStateFile(String filePref, ContentType type) throws FileNotFoundException, IOException {
		String fileUID = getStateFileUID(subLattice.getId(), currentIteration);

		File file = new File(filePref + "_"+ version + "_" + (fileNum++) +"_" + fileUID);
		file.deleteOnExit();

		LBState state = new LBState(fluid, solid);
		state.writeState(file, type);

		return file;
	}
	

	/////////////////////////////
	// Runnable implementation //
	/////////////////////////////
	
	@Override
	public void run() {
		if(subLattice != null) {
			try {
				initProcessingChainElements(simParam.getProcessingChainCopy());
			} catch (LBException e1) {
				printMessage("Error.");
				manager.signalChildError(e1, "LBAlgorithm"+subLattice.getId());
				
				exitActions();
				return;
			}
		} else {
			// null thread -> empty processing chain
			operationChain = new ProcessingChain();
		}
		
		long startTime = System.currentTimeMillis();
		while(currentIteration < toIteration) {
			
			if(currentIteration % 10 == 0)
				manager.getDistributedAgent().log("iteration",
						Integer.toString(currentIteration));
			
			if(isKilled()) {
				exitActions();
				return;
			}
			
			// Apply operators to lattice.
			Iterator<ProcessingChainElement> operationChainIterator =
				operationChain.iterator();
			while(operationChainIterator.hasNext()) {

				ProcessingChainElement currentOperator =
					operationChainIterator.next();

				try {

//					printMessage("Applying "+currentOperator.getClass().getSimpleName());
					currentOperator.apply();

				} catch (InterruptedException e) {

					printMessage("Interrupted.");
					
					exitActions();
					return;

				} catch (BrokenDAException e) {

					DAId daId = e.getBrokenDAId();
					printMessage("Broken DA "+daId);
					manager.signalBroken(new BrokenDA(daId));
					
					exitActions();
					return;

				} catch (Throwable t) {

					printMessage("Error.");
					manager.signalChildError(t, "LBAlgorithm"+subLattice.getId());
					
					exitActions();
					return;
				}

			}

			++currentIteration;
			
			if(isKilled()) {
				exitActions();
				return;
			}

//			printMessage("Iteration "+currentIteration);

			if(isBackupNeeded()) {

				printMessage("Entering backup stage @"+currentIteration);

				try {

					waitBackup();  // Blocks until previous backup is done.

				} catch (InterruptedException e) {

					printMessage("Interrupted.");
					
					exitActions();
					return;

				}

			}

		}
		
		printMessage("LB iterations done.");

		long stopTime = System.currentTimeMillis();
		simDuration = stopTime - startTime;


		// Closing loggers
		operationChain.closeLoggers();
		
		// Final backup if needed
		if(keepLastState && backupRate > 0) {
			
			printMessage("Entering final backup stage @"+currentIteration);
			if(isKilled()) {
				
				exitActions();
				return;
			}

			try {

				waitBackup();  // Blocks until backup is done.

			} catch (InterruptedException e) {

				printMessage("Interrupted.");
				exitActions();
				return;

			}
			
		} else if(keepLastState && subLattice != null) {

			printMessage("Keeping final state for next sim. @"+currentIteration);
			try {
				dumpFinalStateToDFS();
			} catch (InterruptedException e) {
				printMessage("Interrupted.");
				exitActions();
				return;
			} catch (Exception e) {
				manager.signalChildError(e, "LBAlgorithm"+subLattice.getId());
				
				exitActions();
				return;
			}

		}

		// Transfer state file to output if requested
		if(outputClient != null && subLattice != null) {

			printMessage("Final state redirected to output.");
			try {
				
				synchronized(this) {
					outputClientSync = new Semaphore(0);
				}
				File stateFile = generateStateFile(manager.getFilePrefix(),
						ContentType.compress);
				outputClient.put(stateFile, getStateFileUID(subLattice.getId(), toIteration), this);
				outputClientSync.acquire();
				stateFile.delete(); // File transfered, not needed anymore.

			} catch (InterruptedException e) {

				printMessage("Interrupted.");
				
				exitActions();
				return;

			} catch (Exception e) {

				manager.signalChildError(e, "LBAlgorithm "+subLattice.getId());
				exitActions();
				return;
			}

		}

		// Signal end to LBSimulation.
		printMessage("Finished.");
		try {

			manager.algFinished(this);

		} catch (InterruptedException e) {

			printMessage("Interrupted.");
			exitActions();
			return;

		}

		exitActions();
	}
	
	private void exitActions() {
	}

	private boolean isBackupNeeded() {
		return
			(backupRate > 0 &&
					(currentIteration - fromIteration)%backupRate == 0 &&
					currentIteration < toIteration);
	}
	
	
	/////////////////////
	// Private members //
	/////////////////////
	
	private synchronized boolean isKilled() {
		return killFlag;
	}

	private void dumpFinalStateToDFS() throws Exception {
		File stateFile = generateStateFile(manager.getFilePrefix(),
				manager.compressFiles());
		String fileUID = getStateFileUID(subLattice.getId(), currentIteration);
		
		waitFinalDump = new Semaphore(0);
		manager.getDistributedAgent().getFileSystemPeer().addFile(fileUID, stateFile, FileMode.rw, this);
		waitFinalDump.acquire();
	}

	private void initReceptionBuffers() {

		int linkCount = this.fluid.getLatticeDescriptor().getVelocitiesCount();
		receivedBorders = new LinkedBlockingQueue[linkCount];
		for(int i = 0; i < linkCount; ++i) {

			if( ! fluid.getLatticeDescriptor().isRest(i))
				receivedBorders[i] = new LinkedBlockingQueue<BorderDataInfo>();

		}

	}

	private void initProcessingChainElements(ProcessingChain operationChain) throws LBException {

		this.operationChain = operationChain;

		Iterator<ProcessingChainElement> operationChainIterator = operationChain.iterator();
		while(operationChainIterator.hasNext()) {

			ProcessingChainElement op = operationChainIterator.next();
			op.setLBAlgorithm(this);

		}

	}

	private void initDataStructures(int subId, SimulationParameters simParam)
	throws LBException {
		
		this.version = simParam.getVersion();
		this.simNumber = simParam.getSimulationNumber();
		
		if(subId >= 0) {
			this.subLattice = simParam.getSubLattices(subId);
	
			sub2Neighbor = new TreeMap<Integer, MOSAccessorInterface>();
			subL2Link = new TreeMap<Integer, List<Integer>>();
	
			// subL2Link construction
			for(int i = 0; i < subLattice.getVelocitiesCount(); ++i) {
	
				int neighSubL = subLattice.getNeighborFromVel(i);
	
				if(neighSubL < 0)
					continue;
	
				if(subL2Link.containsKey(neighSubL)) {
					subL2Link.get(neighSubL).add(i);
				} else {
					LinkedList<Integer> l = new LinkedList<Integer>(); 
					subL2Link.put(neighSubL, l);
					l.add(i);
				}
			}
		}

		FaultToleranceConfiguration ftConf = simParam.getFaultToleranceConfiguration();
		if(ftConf.replicationIsEnabled()) {
			this.backupRate = simParam.getBackupRate();
		} else {
			this.backupRate = 0;
		}
		this.keepLastState = simParam.getKeepFinalState();
		this.outputClient = simParam.getOutputClient();

		this.fromIteration = simParam.getStartingIteration();
		this.toIteration = simParam.getLastIteration();
		currentIteration = fromIteration;

		waitBackupSem = new Semaphore(0);
	}
	
	private void initDataStructuresForBenchmark() {

		LatticeDescriptor lDesc = fluid.getLatticeDescriptor();

		subLattice = new SubLattice(lDesc);
		subLattice.initAsLattice(fluid.getSize(), lDesc);

		this.version = 0;

		sub2Neighbor = new TreeMap<Integer, MOSAccessorInterface>();
		subL2Link = new TreeMap<Integer, List<Integer>>();

		// subL2Link construction
		for(int i = 0; i < subLattice.getVelocitiesCount(); ++i) {

			int neighSubL = subLattice.getNeighborFromVel(i);
			
			if(neighSubL < 0)
				continue;

			if(subL2Link.containsKey(neighSubL)) {

				subL2Link.get(neighSubL).add(i);

			} else {

				LinkedList<Integer> l = new LinkedList<Integer>(); 
				subL2Link.put(neighSubL, l);
				l.add(i);

			}

		}

		this.backupRate = 0;

		currentIteration = fromIteration;

	}

	private void waitBackup() throws InterruptedException {
		if(subLattice != null) {
			synchronized(this) {
				waitBackupSem = new Semaphore(0);
			}
			manager.replicateState(this);
			waitBackupSem.acquire();
		} // else -> null thread: SKIP
	}

	public void printMessage(String string) {
		manager.printMessage("["+algThread.getName()+"] "+string);
	}
	
	private void restoreStateFromFile(File file, ContentType contentType) throws LBException {

		LBState state = new LBState();
		try {

			state.readState(file, contentType);

		} catch (Exception e) {
			
			throw new LBException("Could not restore state from file.", e);
			
		}

		fluid = state.getLattice();
		solid = state.getSolid();

	}

	private void restoreSolidFromFile(File solidFile) throws LBException {

		try {

			solid = Solid.readBinSolid(solidFile.getAbsolutePath());

		} catch (IOException e) {
			
			throw new LBException("Could not read sub-solid file.", e);
			
		}

	}

	@Override
	public void addFileCB(DFSException error, String fileUID, File file,
			FileMode mode) {
		waitFinalDump.release();
	}

	@Override
	public void deleteFilesCB(Pattern p) {
		manager.signalChildError(new CallBackNotImplemented("deleteFile"), "LBAlgorithm"+subLattice.getId());
	}

	@Override
	public void getFileCB(DFSException error, String fileUID, File f) {
		manager.signalChildError(new CallBackNotImplemented("getFile"), "LBAlgorithm"+subLattice.getId());
	}

	@Override
	public void removeFileCB(DFSException error, String fileUID, File newFile) {
		manager.signalChildError(new CallBackNotImplemented("removeFile"), "LBAlgorithm"+subLattice.getId());
	}

	@Override
	public void replicateFileCB(ReplicateFileResult res) {
		manager.signalChildError(new CallBackNotImplemented("replicateFile"), "LBAlgorithm"+subLattice.getId());
	}

	@Override
	public void close() throws IOException, InterruptedException {
		// No effect
	}

	@Override
	public void writeNonBlockingMessage(Message m) {
		try {
			writeBlockingMessage(m);
		} catch (InterruptedException e) {
		}
	}

	@Override
	public void outputClientPutCB(OutputClientCB params) {
		outputClientSync.release();
	}

	public long getSimDuration() {
		return simDuration;
	}

	@Override
	public DAId getDestinationDAId() {
//		return manager.getDistributedAgent().getDaId();
		return thisDaId;
	}

	public int getCurrentOffset() {
		return currentIteration - fromIteration;
	}

	@Override
	public void submitIncomingMessage(Message msg) {
		try {
			writeBlockingMessage(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private int numOfAwaitedSent;
	private Semaphore awaitedSent;

	public synchronized void setAwaitedSentSem(int num) {
		this.numOfAwaitedSent = num;
		awaitedSent = new Semaphore(0);
	}

	public void awaitedSentWait() throws InterruptedException {
		awaitedSent.acquire(numOfAwaitedSent);
	}

	public void releaseAwaitedSent() {
		awaitedSent.release();
	}

	@Override
	public void signalBroken(BrokenDA bda) {
		kill();
	}

	@Override
	public void signalSent(Message m, boolean success) {
		if(! success) {
			kill();
			return;
		}
		
		// Only LBData messages sent by NonBlockingBorderSender
		// have a callback.
		releaseAwaitedSent();
	}

	public CommunicatorInterface getCommunicator() {
		return manager.getCommunicator();
	}

}

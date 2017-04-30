package nachos.userprog;

import nachos.machine.*;
import nachos.threads.Condition2;
import nachos.threads.KThread;
import nachos.threads.Lock;
import nachos.threads.ThreadedKernel;

import java.io.EOFException;
import java.util.HashMap;

/**
 * Encapsulates the state of a user process that is not contained in its
 * user thread (or threads). This includes its address translation state, a
 * file table, and information about the program being executed.
 * <p>
 * <p>
 * This class is extended by other classes to support additional functionality
 * (such as additional syscalls).
 *
 * @see nachos.vm.VMProcess
 * @see nachos.network.NetProcess
 */
public class UserProcess
{
    static int numP = 0;
    static int numRunning = 0;
    int pid = 0;
    UserProcess daddy = null;
    Integer exitStatus = null;
    HashMap<Integer, UserProcess> children = new HashMap<Integer, UserProcess>();
    private OpenFile[] openFiles = new OpenFile[16];
    private Lock joinLock = new Lock();
    private Condition2 joinedThreads = new Condition2(joinLock);


    /**
     * Allocate a new process.
     */
    public UserProcess()
    {
        openFiles[0] = UserKernel.console.openForReading();
        openFiles[1] = UserKernel.console.openForWriting();
        pid = numP++;
    }

    /**
     * Allocate and return a new process of the correct class. The class name
     * is specified by the <tt>nachos.conf</tt> key
     * <tt>Kernel.processClassName</tt>.
     *
     * @return a new process of the correct class.
     */
    public static UserProcess newUserProcess()
    {
        numRunning++;
        return (UserProcess) Lib.constructObject(Machine.getProcessClassName());
    }

    /**
     * Execute the specified program with the specified arguments. Attempts to
     * load the program, and then forks a thread to run it.
     *
     * @param name the name of the file containing the executable.
     * @param args the arguments to pass to the executable.
     * @return <tt>true</tt> if the program was successfully executed.
     */
    public boolean execute(String name, String[] args)
    {
        if (!load(name, args))
            return false;

        new UThread(this).setName(name).fork();

        return true;
    }

    /**
     * Save the state of this process in preparation for a context switch.
     * Called by <tt>UThread.saveState()</tt>.
     */
    public void saveState()
    {
    }

    /**
     * Restore the state of this process after a context switch. Called by
     * <tt>UThread.restoreState()</tt>.
     */
    public void restoreState()
    {
        Machine.processor().setPageTable(pageTable);
    }

    /**
     * Read a null-terminated string from this process's virtual memory. Read
     * at most <tt>maxLength + 1</tt> bytes from the specified address, search
     * for the null terminator, and convert it to a <tt>java.lang.String</tt>,
     * without including the null terminator. If no null terminator is found,
     * returns <tt>null</tt>.
     *
     * @param vaddr     the starting virtual address of the null-terminated
     *                  string.
     * @param maxLength the maximum number of characters in the string,
     *                  not including the null terminator.
     * @return the string read, or <tt>null</tt> if no null terminator was
     * found.
     */
    public String readVirtualMemoryString(int vaddr, int maxLength)
    {
        Lib.assertTrue(maxLength >= 0);

        byte[] bytes = new byte[maxLength + 1];

        int bytesRead = readVirtualMemory(vaddr, bytes);

        for (int length = 0; length < bytesRead; length++)
        {
            if (bytes[length] == 0)
                return new String(bytes, 0, length);
        }

        return null;
    }

    /**
     * Transfer data from this process's virtual memory to all of the specified
     * array. Same as <tt>readVirtualMemory(vaddr, data, 0, data.length)</tt>.
     *
     * @param vaddr the first byte of virtual memory to read.
     * @param data  the array where the data will be stored.
     * @return the number of bytes successfully transferred.
     */
    public int readVirtualMemory(int vaddr, byte[] data)
    {
        return readVirtualMemory(vaddr, data, 0, data.length);
    }

    /**
     * Transfer data from this process's virtual memory to the specified array.
     * This method handles address translation details. This method must
     * <i>not</i> destroy the current process if an error occurs, but instead
     * should return the number of bytes successfully copied (or zero if no
     * data could be copied).
     *
     * @param vaddr  the first byte of virtual memory to read.
     * @param data   the array where the data will be stored.
     * @param offset the first byte to write in the array.
     * @param length the number of bytes to transfer from virtual memory to
     *               the array.
     * @return the number of bytes successfully transferred.
     */
    public int readVirtualMemory(int vaddr, byte[] data, int offset,
                                 int length)
    {
        Lib.assertTrue(offset >= 0 && length >= 0 && offset + length <= data.length);

        if (!isValidAddress(vaddr))
            return 0;

        int page = Processor.pageFromAddress(vaddr);
        int pageOffset = Processor.offsetFromAddress(vaddr);
//        System.out.printf("Reading: Address %d; Page: %d; Offset: %d; Physical: %d%n", vaddr, page, pageOffset,
//                pageTable[page].ppn);


        byte[] memory = Machine.processor().getMemory();

        int toCopy = Math.min(length, pageSize - pageOffset);
        int amount = toCopy;
        System.arraycopy(memory, Processor.makeAddress(pageTable[page++].ppn, pageOffset), data, offset, toCopy);


        while (amount < length && page < numPages)
        {
            toCopy = Math.min(length - amount, pageSize);
            System.arraycopy(memory, Processor.makeAddress(pageTable[page++].ppn, 0), data, offset + amount, toCopy);
            amount += toCopy;
        }

        return amount;
    }

    /**
     * Transfer all data from the specified array to this process's virtual
     * memory.
     * Same as <tt>writeVirtualMemory(vaddr, data, 0, data.length)</tt>.
     *
     * @param vaddr the first byte of virtual memory to write.
     * @param data  the array containing the data to transfer.
     * @return the number of bytes successfully transferred.
     */
    public int writeVirtualMemory(int vaddr, byte[] data)
    {
        return writeVirtualMemory(vaddr, data, 0, data.length);
    }

    /**
     * Transfer data from the specified array to this process's virtual memory.
     * This method handles address translation details. This method must
     * <i>not</i> destroy the current process if an error occurs, but instead
     * should return the number of bytes successfully copied (or zero if no
     * data could be copied).
     *
     * @param vaddr  the first byte of virtual memory to write.
     * @param data   the array containing the data to transfer.
     * @param offset the first byte to transfer from the array.
     * @param length the number of bytes to transfer from the array to
     *               virtual memory.
     * @return the number of bytes successfully transferred.
     */
    public int writeVirtualMemory(int vaddr, byte[] data, int offset,
                                  int length)
    {
        Lib.assertTrue(offset >= 0 && length >= 0 && offset + length <= data.length);

        if (!isValidAddress(vaddr))
            return 0;

        int page = Processor.pageFromAddress(vaddr);
        int pageOffset = Processor.offsetFromAddress(vaddr);
        int toCopy = Math.min(length, pageSize - pageOffset);
//        System.out.printf("Writing: Address %d; Page: %d; Offset: %d; Physical: %d%n", vaddr, page, Processor
// .offsetFromAddress(vaddr), pageTable[page].ppn);
//        System.out.println(Lib.bytesToString(data, offset, length));

        byte[] memory = Machine.processor().getMemory();

        System.arraycopy(data, offset, memory, Processor.makeAddress(pageTable[page++].ppn, pageOffset), toCopy);
        int amount = toCopy; // Amount already copied
        while (amount < length && page < numPages)
        {
            toCopy = Math.min(length - amount, pageSize);
            System.arraycopy(data, offset + amount, memory, Processor.makeAddress(pageTable[page++].ppn, 0), toCopy);
            amount += toCopy;
        }

        return amount;
    }

    /**
     * Load the executable with the specified name into this process, and
     * prepare to pass it the specified arguments. Opens the executable, reads
     * its header information, and copies sections and arguments into this
     * process's virtual memory.
     *
     * @param name the name of the file containing the executable.
     * @param args the arguments to pass to the executable.
     * @return <tt>true</tt> if the executable was successfully loaded.
     */
    private boolean load(String name, String[] args)
    {
        Lib.debug(dbgProcess, "UserProcess.load(\"" + name + "\")");

        OpenFile executable = ThreadedKernel.fileSystem.open(name, false);
        if (executable == null)
        {
            Lib.debug(dbgProcess, "\topen failed");
            return false;
        }

        try
        {
            coff = new Coff(executable);
        } catch (EOFException e)
        {
            executable.close();
            Lib.debug(dbgProcess, "\tcoff load failed");
            return false;
        }

        // make sure the sections are contiguous and start at page 0
        numPages = 0;
        for (int s = 0; s < coff.getNumSections(); s++)
        {
            CoffSection section = coff.getSection(s);
            if (section.getFirstVPN() != numPages)
            {
                coff.close();
                Lib.debug(dbgProcess, "\tfragmented executable");
                return false;
            }
            numPages += section.getLength();
        }

        // make sure the argv array will fit in one page
        byte[][] argv = new byte[args.length][];
        int argsSize = 0;
        for (int i = 0; i < args.length; i++)
        {
            argv[i] = args[i].getBytes();
            // 4 bytes for argv[] pointer; then string plus one for null byte
            argsSize += 4 + argv[i].length + 1;
        }
        if (argsSize > pageSize)
        {
            coff.close();
            Lib.debug(dbgProcess, "\targuments too long");
            return false;
        }

        // program counter initially points at the program entry point
        initialPC = coff.getEntryPoint();

        // next comes the stack; stack pointer initially points to top of it
        numPages += stackPages;
        initialSP = numPages * pageSize;

        // and finally reserve 1 page for arguments
        numPages++;

        if (!loadSections())
            return false;

        // store arguments in last page
        int entryOffset = (numPages - 1) * pageSize;
        int stringOffset = entryOffset + args.length * 4;

        this.argc = args.length;
        this.argv = entryOffset;

        for (int i = 0; i < argv.length; i++)
        {
            byte[] stringOffsetBytes = Lib.bytesFromInt(stringOffset);
            Lib.assertTrue(writeVirtualMemory(entryOffset, stringOffsetBytes) == 4);
            entryOffset += 4;
            Lib.assertTrue(writeVirtualMemory(stringOffset, argv[i]) == argv[i].length);
            stringOffset += argv[i].length;
            Lib.assertTrue(writeVirtualMemory(stringOffset, new byte[]{0}) == 1);
            stringOffset += 1;
        }

        return true;
    }

    /**
     * Allocates memory for this process, and loads the COFF sections into
     * memory. If this returns successfully, the process will definitely be
     * run (this is the last step in process initialization that can fail).
     *
     * @return <tt>true</tt> if the sections were successfully loaded.
     */
    protected boolean loadSections()
    {
        if (numPages > Machine.processor().getNumPhysPages() || numPages > UserKernel.getFreePages())
        {
            coff.close();
            Lib.debug(dbgProcess, "\tinsufficient physical memory");
            return false;
        }

        pageTable = new TranslationEntry[numPages];
        int numAllocatedPages = 0;

        // load sections
        for (int s = 0; s < coff.getNumSections(); s++)
        {
            CoffSection section = coff.getSection(s);


            Lib.debug(dbgProcess, "\tinitializing " + section.getName()
                    + " section (" + section.getLength() + " pages)");

            for (int i = 0; i < section.getLength(); i++)
            {
                pageTable[numAllocatedPages] = new TranslationEntry(numAllocatedPages++, UserKernel.freePages.remove(),
                        true, section.isReadOnly(), true, false);

                // for now, just assume virtual addresses=physical addresses
                section.loadPage(i, pageTable[section.getFirstVPN() + i].ppn);
            }
        }

        for (int i = numAllocatedPages; i < numPages; i++)
            pageTable[i] = new TranslationEntry(i, UserKernel.freePages.remove(), true, false, false, false);

        return true;
    }

    /**
     * Release any resources allocated by <tt>loadSections()</tt>.
     */
    protected void unloadSections()
    {
        boolean intStatus = Machine.interrupt().disable();
        for (int i = 0; i < numPages; i++)
        {
            UserKernel.freePages.add(pageTable[i].ppn);
            pageTable[i].valid = false;
        }
        Machine.interrupt().restore(intStatus);
    }

    /**
     * Initialize the processor's registers in preparation for running the
     * program loaded into this process. Set the PC register to point at the
     * start function, set the stack pointer register to point at the top of
     * the stack, set the A0 and A1 registers to argc and argv, respectively,
     * and initialize all other registers to 0.
     */
    public void initRegisters()
    {
        Processor processor = Machine.processor();

        // by default, everything's 0
        for (int i = 0; i < processor.numUserRegisters; i++)
            processor.writeRegister(i, 0);

        // initialize PC and SP according
        processor.writeRegister(Processor.regPC, initialPC);
        processor.writeRegister(Processor.regSP, initialSP);

        // initialize the first two argument registers to argc and argv
        processor.writeRegister(Processor.regA0, argc);
        processor.writeRegister(Processor.regA1, argv);
    }

    /**
     * Handle the halt() system call.
     */
    private int handleHalt()
    {
        if (pid != 0)
            return -1;
        Machine.halt();

        Lib.assertNotReached("Machine.halt() did not halt machine!");
        return 0;
    }

    private boolean isValidAddress(int address)
    {
        int page = Processor.pageFromAddress(address);
//        return address >= 0 && address < numPages * pageSize;
        return 0 <= page && page < numPages;
    }

    private boolean isValidFileDescriptor(int descriptor)
    {
        return descriptor >= 0 && descriptor < openFiles.length && openFiles[descriptor] != null;
    }

    private int getOpenFileDescriptor()
    {
        for (int i = 0; i < openFiles.length; i++)
            if (openFiles[i] == null)
                return i;
        return -1;
    }

    private int assignFileDescriptor(OpenFile file)
    {
        int descriptor = getOpenFileDescriptor();
        if (descriptor < 0)
            return -1;
        openFiles[descriptor] = file;
        return descriptor;
    }

    private int openFile(String filename, boolean create)
    {
        if (filename == null)
            return -1;

        OpenFile newFile = ThreadedKernel.fileSystem.open(filename, create);
        if (newFile == null)
            return -1;
        return assignFileDescriptor(newFile);
    }

    /**
     * Terminate the current process immediately. Any open file descriptors
     * belonging to the process are closed. Any children of the process no longer
     * have a parent process.
     * <p>
     * status is returned to the parent process as this process's handleExit status and
     * can be collected using the join syscall. A process exiting normally should
     * (but is not required to) set status to 0.
     * <p>
     * handleExit() never returns.
     *
     * @param status
     */
    public void handleExit(int status)
    {
        if (!joinLock.isHeldByCurrentThread())
            joinLock.acquire();

        for (int i = 0; i < openFiles.length; i++)
            handleClose(i);

        for (UserProcess child : children.values())
            children.remove(child.pid).daddy = null;

        children = null;
        this.exitStatus = status;
        unloadSections();

        joinedThreads.wakeAll();
        joinLock.release();

        boolean intStatus = Machine.interrupt().disable();

        if (--numRunning == 0) // Last one; halt
            Machine.halt();

        Machine.interrupt().restore(intStatus);
        KThread.finish();
    }

    /**
     * Execute the program stored in the specified file, with the specified
     * arguments, in a new child process. The child process has a new unique
     * process ID, and starts with stdin opened as file descriptor 0, and stdout
     * opened as file descriptor 1.
     * <p>
     * file is a null-terminated string that specifies the name of the file
     * containing the executable. Note that this string must include the ".coff"
     * extension.
     * <p>
     * argc specifies the number of arguments to pass to the child process. This
     * number must be non-negative.
     * <p>
     * argv is an array of pointers to null-terminated strings that represent the
     * arguments to pass to the child process. argv[0] points to the first
     * argument, and argv[argc-1] points to the last argument.
     * <p>
     * exec() returns the child process's process ID, which can be passed to
     * join(). On error, returns -1.
     */
    public int handleExec(int file, int argc, int argv)
    {
        String name;
        if (!(argc >= 0 && isValidAddress(file) && ((name = readVirtualMemoryString(file, 256)) != null) &&
                name.endsWith(".coff")))
            return -1;

        String[] charStars = new String[argc];
        byte[] dumArray = new byte[4];
        for (int i = 0; i < argc; i++)
            if (!(isValidAddress(argv + 4 * i) && readVirtualMemory(argv + 4 * i, dumArray) == 4 &&
                    (charStars[i] = readVirtualMemoryString(Lib.bytesToInt(dumArray, 0), 256)) != null))
                return -1;

        UserProcess child = newUserProcess();
        child.daddy = this;
        children.put(child.pid, child);

        return child.execute(name, charStars) ? child.pid : -1;
    }

    /**
     * Does not return until the child is finished exiting.
     */
    private void join()
    {
        if (!joinLock.isHeldByCurrentThread())
            joinLock.acquire();

        while (exitStatus == null)
            joinedThreads.sleep();

        joinLock.release();
    }

    /**
     * Suspend execution of the current process until the child process specified
     * by the processID argument has exited. If the child has already exited by the
     * time of the call, returns immediately. When the current process resumes, it
     * disowns the child process, so that join() cannot be used on that process
     * again.
     * <p>
     * processID is the process ID of the child process, returned by exec().
     * <p>
     * status points to an integer where the handleExit status of the child process will
     * be stored. This is the value the child passed to handleExit(). If the child exited
     * because of an unhandled exception, the value stored is not defined.
     * <p>
     * If the child exited normally, returns 1. If the child exited as a result of
     * an unhandled exception, returns 0. If processID does not refer to a child
     * process of the current process, returns -1.
     *
     * @param processID
     * @param status
     */
    public int handleJoin(int processID, int status)
    {
        if (!isValidAddress(status))
            return -1;


        UserProcess child = children.remove(processID);
        if (child == null)
            return -1;

        child.join();

        int upStatus = child.exitStatus;
        writeVirtualMemory(status, Lib.bytesFromInt(upStatus));
        return upStatus == 0 ? 1 : 0;
    }

    public int handleCreate(int address)
    {
        return isValidAddress(address) ? openFile(readVirtualMemoryString(address, 256), true) : -1;

    }

    public int handleOpen(int address)
    {
        return isValidAddress(address) ? openFile(readVirtualMemoryString(address, 256), false) : -1;
    }


    /**
     * Attempt to read up to count bytes into buffer from the file or stream
     * referred to by fileDescriptor.
     * <p>
     * On success, the number of bytes read is returned. If the file descriptor
     * refers to a file on disk, the file position is advanced by this number.
     * <p>
     * It is not necessarily an error if this number is smaller than the number of
     * bytes requested. If the file descriptor refers to a file on disk, this
     * indicates that the end of the file has been reached. If the file descriptor
     * refers to a stream, this indicates that the fewer bytes are actually
     * available right now than were requested, but more bytes may become available
     * in the future. Note that read() never waits for a stream to have more data;
     * it always returns as much as possible immediately.
     * <p>
     * On error, -1 is returned, and the new file position is undefined. This can
     * happen if fileDescriptor is invalid, if part of the buffer is read-only or
     * invalid, or if a network stream has been terminated by the remote host and
     * no more data is available.
     *
     * @param fileDescriptor
     * @param buffer
     * @param count
     */
    public int handleRead(int fileDescriptor, int buffer, int count)
    {
        if (!isValidFileDescriptor(fileDescriptor) || !isValidAddress(buffer) || !isValidAddress(buffer + count - 1))
            return -1;

        byte[] buf = new byte[count];
        int bytesRead = openFiles[fileDescriptor].read(buf, 0, count);
        if (bytesRead < 0)
            return -1;
        writeVirtualMemory(buffer, buf, 0, bytesRead);
        return bytesRead;
    }

    /**
     * Attempt to write up to count bytes from buffer to the file or stream
     * referred to by fileDescriptor. write() can return before the bytes are
     * actually flushed to the file or stream. A write to a stream can block,
     * however, if kernel queues are temporarily full.
     * <p>
     * On success, the number of bytes written is returned (zero indicates nothing
     * was written), and the file position is advanced by this number. It IS an
     * error if this number is smaller than the number of bytes requested. For
     * disk files, this indicates that the disk is full. For streams, this
     * indicates the stream was terminated by the remote host before all the data
     * was transferred.
     * <p>
     * On error, -1 is returned, and the new file position is undefined. This can
     * happen if fileDescriptor is invalid, if part of the buffer is invalid, or
     * if a network stream has already been terminated by the remote host.
     *
     * @param fileDescriptor
     * @param buffer
     * @param count
     */
    public int handleWrite(int fileDescriptor, int buffer, int count)
    {
        if (!isValidFileDescriptor(fileDescriptor) || !isValidAddress(buffer) || !isValidAddress(buffer + count))
            return -1;
        byte[] buf = new byte[count];
        int bytesRead = readVirtualMemory(buffer, buf);
        openFiles[fileDescriptor].write(buf, 0, bytesRead);
        return bytesRead;
    }

    /**
     * Close a file descriptor, so that it no longer refers to any file or stream
     * and may be reused.
     * <p>
     * If the file descriptor refers to a file, all data written to it by write()
     * will be flushed to disk before close() returns.
     * If the file descriptor refers to a stream, all data written to it by write()
     * will eventually be flushed (unless the stream is terminated remotely), but
     * not necessarily before close() returns.
     * <p>
     * The resources associated with the file descriptor are released. If the
     * descriptor is the last reference to a disk file which has been removed using
     * unlink, the file is deleted (this detail is handled by the file system
     * implementation).
     * <p>
     * Returns 0 on success, or -1 if an error occurred.
     *
     * @param fileDescriptor
     */
    public int handleClose(int fileDescriptor)
    {
        if (!isValidFileDescriptor(fileDescriptor))
            return -1;
        openFiles[fileDescriptor].close();
        openFiles[fileDescriptor] = null;
        return 0;
    }

    /**
     * Delete a file from the file system. If no processes have the file open, the
     * file is deleted immediately and the space it was using is made available for
     * reuse.
     * <p>
     * If any processes still have the file open, the file will remain in existence
     * until the last file descriptor referring to it is closed. However, creat()
     * and open() will not be able to return new file descriptors for the file
     * until it is deleted.
     * <p>
     * Returns 0 on success, or -1 if an error occurred.
     *
     * @param name
     */
    public int handleUnlink(int name)
    {
        String val;
        if (isValidAddress(name) && ((val = readVirtualMemoryString(name, 256)) != null) &&
                ThreadedKernel.fileSystem.remove(val))
            return 0;
        else
            return -1;
    }

    /**
     * Map the file referenced by fileDescriptor into memory at address. The file
     * may be as large as 0x7FFFFFFF bytes.
     * <p>
     * To maintain consistency, further calls to read() and write() on this file
     * descriptor will fail (returning -1) until the file descriptor is closed.
     * <p>
     * When the file descriptor is closed, all remaining dirty pages of the map
     * will be flushed to disk and the map will be removed.
     * <p>
     * Returns the length of the file on success, or -1 if an error occurred.
     *
     * @param fileDescriptor
     * @param address
     */
    public int handleMmap(int fileDescriptor, int address)
    {
        return 0;
    }

    /**
     * Attempt to initiate a new connection to the specified port on the specified
     * remote host, and return a new file descriptor referring to the connection.
     * connect() does not give up if the remote host does not respond immediately.
     * <p>
     * Returns the new file descriptor, or -1 if an error occurred.
     *
     * @param host
     * @param port
     */
    public int handleConnect(int host, int port)
    {
        return 0;
    }

    /**
     * Attempt to accept a single connection on the specified local port and return
     * a file descriptor referring to the connection.
     * <p>
     * If any connection requests are pending on the port, one request is dequeued
     * and an acknowledgement is sent to the remote host (so that its connect()
     * call can return). Since the remote host will never cancel a connection
     * request, there is no need for accept() to wait for the remote host to
     * confirm the connection (i.e. a 2-way handshake is sufficient; TCP's 3-way
     * handshake is unnecessary).
     * <p>
     * If no connection requests are pending, returns -1 immediately.
     * <p>
     * In either case, accept() returns without waiting for a remote host.
     * <p>
     * Returns a new file descriptor referring to the connection, or -1 if an error
     * occurred.
     *
     * @param port
     */
    public int handleAccept(int port)
    {
        return 0;
    }


    private static final int
            syscallHalt = 0,
            syscallExit = 1,
            syscallExec = 2,
            syscallJoin = 3,
            syscallCreate = 4,
            syscallOpen = 5,
            syscallRead = 6,
            syscallWrite = 7,
            syscallClose = 8,
            syscallUnlink = 9;

    /**
     * Handle a syscall exception. Called by <tt>handleException()</tt>. The
     * <i>syscall</i> argument identifies which syscall the user executed:
     * <p>
     * <table>
     * <tr><td>syscall#</td><td>syscall prototype</td></tr>
     * <tr><td>0</td><td><tt>void halt();</tt></td></tr>
     * <tr><td>1</td><td><tt>void handleExit(int status);</tt></td></tr>
     * <tr><td>2</td><td><tt>int  exec(char *name, int argc, char **argv);
     * </tt></td></tr>
     * <tr><td>3</td><td><tt>int  join(int pid, int *status);</tt></td></tr>
     * <tr><td>4</td><td><tt>int  create(char *name);</tt></td></tr>
     * <tr><td>5</td><td><tt>int  open(char *name);</tt></td></tr>
     * <tr><td>6</td><td><tt>int  read(int fd, char *buffer, int size);
     * </tt></td></tr>
     * <tr><td>7</td><td><tt>int  write(int fd, char *buffer, int size);
     * </tt></td></tr>
     * <tr><td>8</td><td><tt>int  close(int fd);</tt></td></tr>
     * <tr><td>9</td><td><tt>int  unlink(char *name);</tt></td></tr>
     * </table>
     *
     * @param syscall the syscall number.
     * @param a0      the first syscall argument.
     * @param a1      the second syscall argument.
     * @param a2      the third syscall argument.
     * @param a3      the fourth syscall argument.
     * @return the value to be returned to the user.
     */
    public int handleSyscall(int syscall, int a0, int a1, int a2, int a3)
    {
        switch (syscall)
        {
            case syscallHalt:
                return handleHalt();

            case syscallExit:
                handleExit(a0);
                break;

            case syscallExec:
                return handleExec(a0, a1, a2);

            case syscallJoin:
                return handleJoin(a0, a1);

            case syscallCreate:
                return handleCreate(a0);

            case syscallOpen:
                return handleOpen(a0);

            case syscallRead:
                return handleRead(a0, a1, a2);

            case syscallWrite:
                return handleWrite(a0, a1, a2);

            case syscallClose:
                return handleClose(a0);

            case syscallUnlink:
                return handleUnlink(a0);

            default:
                Lib.debug(dbgProcess, "Unknown syscall " + syscall);
                Lib.assertNotReached("Unknown system call!");
        }
        return 0;
    }

    /**
     * Handle a user exception. Called by
     * <tt>UserKernel.exceptionHandler()</tt>. The
     * <i>cause</i> argument identifies which exception occurred; see the
     * <tt>Processor.exceptionZZZ</tt> constants.
     *
     * @param cause the user exception that occurred.
     */
    public void handleException(int cause)
    {
        Processor processor = Machine.processor();

        switch (cause)
        {
            case Processor.exceptionSyscall:
                int result = handleSyscall(processor.readRegister(Processor.regV0),
                        processor.readRegister(Processor.regA0),
                        processor.readRegister(Processor.regA1),
                        processor.readRegister(Processor.regA2),
                        processor.readRegister(Processor.regA3)
                );
                processor.writeRegister(Processor.regV0, result);
                processor.advancePC();
                break;

            default:
                Lib.debug(dbgProcess, "Unexpected exception: " +
                        Processor.exceptionNames[cause]);
                Lib.assertNotReached("Unexpected exception");
        }
    }

    /**
     * The program being run by this process.
     */
    protected Coff coff;

    /**
     * This process's page table.
     */
    protected TranslationEntry[] pageTable;
    /**
     * The number of contiguous pages occupied by the program.
     */
    protected int numPages;

    /**
     * The number of pages in the program's stack.
     */
    protected final int stackPages = 8;

    private int initialPC, initialSP;
    private int argc, argv;

    private static final int pageSize = Processor.pageSize;
    private static final char dbgProcess = 'a';
}

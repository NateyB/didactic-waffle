# Mon, January 23
* UNIX
  * Exact duplicate of processes can be made with the fork() command,
  and both the parent and child processes continue after the fork,
  which for the parent returns the pid of the child and returns 0
  for the child
* Windows
  * There is another option, which allows a new program to be loaded to
  the process' memory. This call is execlp(); only Windows supports both

## Process termination
* (Under normal circumstances), when a process finishes executing
  * Asks the OS to delete it (and deallocate resources) using to exit()
  * At this point, the process may return data (such as exit code) to parent
  * _All_ resources deallocated
* Termination may also occur under additional circumstances
  * Process may terminate other process using the abort() system call
    * Typically, the process must be a parent to do that
  * Occurs when a child exceeds its use of resources, is no longer relevant,
    or the parent expires
* Cooperating Processes
  * Classification concurrently running processes
  * Independent: Cannot be affected by or affect other programs
  * Cooperating: Can affect or be affected by other programs
    * Benefits:
      * Data sharing, modularity, speed-up, convenience, etc.
  * Concurrency requires communication & synchronization
    * Producer-consumer problem: Paradigm for cooperating processes
      * Producer produces information that is consumed by a consumer process
      * Examples: Print, a compiler
      * Need a buffer of items that can be filled by a producer and emptied
        by a consumer
      * Issue: Producer may produce an item while a consumer wants to use it
        * Okay for producer to make an item while consumer is using a different
          one
    * Unbounded-buffer producer-consumer problem: No limit on buffer size
      * No need for producers to wait
    * Bounded buffer: Fixed size; both must wait at some point

# Fri, January 27
* Synchronization
  * For some unknown reason, the code that we examined when looking at
    thread buffers uses modulo to check buffer size, rather than > or <
  * Message passing
    * Processes communicate without the need to share data
    * Operations
      * Send
      * Receive
          * Fixed-length messages -> Easy to implement, more difficult to use
          * Variable-length -> Difficult to implement, more powerful
    * A communication link must exist between two communicating processes
      * Direct or indirect communication
      * Symmetric/Asymmetric
      * Automatic & Explicit buffering
      * Send by copy or send by reference
      * Fixed-size or variable-length
    * Direct communication
      * Symmetric: send(P, msg) recieve(Q, msg)—both processes must identify
        the other
      * Asymmetric: When you receive, you're not explicitly naming the sender
      * Disadvantage: Changing the name of a process is a problem
    * Indirect communication
      * Messages sent to/received from ports/mailbaxes
      * Two processes can communicate only if they share a mailbox
        * send(A, msg); receive(A, msg)—same mailbox
      * Enables more than two processes to share a mailbox; possible resolution
        * All receive
        * OS decides arbitrarily
        * Only one process permitted to request access at the same time
      * Send/receive synchronization: Implementation options
        * Blocking send: Sending process is blocked until received
        * Non-blocking send: Sending process is _not_ blocked (shocking)
        * Blocking receive: Wait until message is received
        * Non-blocking receive: Do _not_ wait until message is received
        * Special names
          * Rendez-vous: Blocking send & blocking receive
        * Buffering
         * 0 capacity: Cannot save or store messages
         * Bounded capacity: Can save or store n messages; sender blocks if full
         * Unbounded capacity: Can save or store unlimited messages
     * Examples of inter-process communication (IPC)
       * POSIX API (shared, message passing)
         * Portable Operating System Interface for UniX
         * Shared memory
           * Shared memory segment shmget [ID, size, mode]
           * Process must atach segment [shmat()][ID]
           * Access shared memory as needed
           * Detach segment when no longer needed [shmdt()]

# Mon, January 30
* Threads (also called lightweight processes (LWP))
  * Comprises: Thread ID, program counter, register set, and stack
  * Shares with other threads that belong to the same process:
    * Code section
    * Data section
    * Some OS resources (like files)
  * Traditional process: Single-threaded
  * Now, thanks to multiple threads, we can do multiple things at the same time
  * Many packages/apps are multithreaded (web browser, server, word processor)
  * Benefits
    * Responsiveness (apps keep running even if some threads are blocked)
    * Resource sharing (efficient)
    * Economical (saves memory/resources); more economical to create/switch
      than a process
      * Example: Solaris 2: Creating a process is thirty times slower than a
        thread
  * User and Kernel Threads
    * Thread support: User (libraries), Kernel (system)
    * User threads
      * Supported above kernel
      * Implemented by a library (NO support from the kernel)
      * Advantage: Fast compared to create/manage compared to kernel threads
      * Drawback: If kernel is single-threaded and user-level kernel makes a
                  blocking system call, then the **entire process blocks**
    * Kernel threads
      * Supported directly by kernel (kernel => thread creation/execution
        scheduling)
      * Disadvantage: Comparably slow compared to user-level support
      * Advantage: The kernel can continue even if a blocking call is made
  * Threading Models
    * Many-to-one (user to kernel)
      * Advantage: Thread management done at the user-level
      * Disadvantages: 1) Blocking system call, and 2) Unable to run other
        processes in a multithreaded system
    * One-to-one (every user thread creates a kernel thread)
      * Advantages: More concurrency, non-blocking, good use of multiprocessor
      * Disadvantages: Need to create kernel threads, which might be limited
        & overhead
    * Many-to-many (some user threads connected to a kernel thread)
      * \#User threads >= #kernel threads
      * Does not suffer the shortcomings of the other models

# Wed, February 1
* Things to consider:
  * Fork: Need to revisit semantics
    * Issue: If a thread calls fork duplicate _all_ threads in a process; new
      process single-threaded?
      * Solution: Different versions of fork
  * Thread cancellation: Task of terminating thread _before_ it has completed
    * Target thread: Thread to be cancelled
    * Two types of cancellation
      * Asynchronous: Can have a problem with resources
      * Deferred: Target thread checks periodically if it should terminate
    * Pthread: Cancellation point
      * Most OSes allow for async/deferred cancellation
  * Signal handling (signals used in UNIX to notify process of event)
    * Signals follow this pattern
      1. Signal is generated (occurrence of event)
      2. Generated signal delivered to a process
      3. Signal is then processed
    * Synchronous: Delivered to the same process that caused the signal
    * Asynchronous: Delivered to other processes
    * Signal handler
      * Default handler
      * User-defined
      * Handling signals in single-threaded processes → Straightforward
      * Multi-threaded:
        * Deliver to the thread to which the signal applies
        * Deliver to every thread in the process (ctrl-c)
        * Deliver signal to certain threads (specify block or accept signal)
        * Assign a specific thread to handle all signals
  * Thread pool: Ability to control number of threads
  * Java threads
    * Java threads managed by the JVM
    * ALL Java programs comprise at least one single thread of control (main)
    * Thread creation (two ways)
      * Extend _Thread_ and override `run()`
      * Implement _Runnable_ and use it as an argument to thread constructor
    * Thread creation does _not_ equal thread execution; we call `start()` and
      the JVM will call `run()` for us

# Mon, February 6
* C-section problem
  * Design a protocol that processes use to cooperate
  * Each process must requuest permission to enter its critical section
  * Permissiion request done in entry section
  * Critical section may be followed by an exit section
  * Remaining code => Remainder section
* Typical process:
      Do {
        entry section
        critical section
        exit section
        remainder section
      } while (true)
* Any solution must satisfy these requirements
  1. Mutual exclusion: If P_i executing C-section, no other P_j is (i ≠ j)
  2. Progress: If no process is executing its C-section, and some processes
     wish to enter their critical sections, then only those processes
     **not** executing in their remainder section can participate in the
     decision and the selection cannot be postponed indefinitely
  3. Bounded waiting: There exists a bound on the number of times that other
     processes are allowed to enter their C-section after a process has made
     a request and before the request is granted
* 2-Process Solutions (Proposed) (These algorithms are horribly lacking detail):
  1. First solution (the turn variable is shared):
         do {
           while (turn ≠ i)
             critical_section
             turn = j
             remainder_section
         } while (true)
    * Mutual exclusion: Satisfied
    * Progress: Not satisfied
  2. Boolean flag[2]
         do {
           flag[i] = true
           while flag[j]
             C-section
           flag[i] = false
           remainder
         } while (true)
    * Mutual exclusion: Satisfied
    * Progress: Not satisfied
  3. Peterson's Solution (two processes) (turn, flag[])
         do {
           flag[i] = true
           turn = j
           while (flag[j] && turn = j)
             C-section
           flag[i] = false
           remainder
         } while (true)
    * Mutual exclusion: Satisfied
    * Progess: Satisfied
    * Bounded waiting: Satisfied
  4. Bakery's Algorithm (n processes): Numbered tickets & waiting
* Synchronization primitives
  * Lock
    * Two states: Busy/free
    * Two atomic operations
      * Acquire: Wait until lock is free and set it to busy
      * Release: Set the lock free

# Wed, February 8
* Synchronization primitives (continued)
  * Test And Set (guaranteed atomicity)
         boolean TestAndSet(boolean &target)
	     {
	       boolean rv = target;
	       target = true;
	       return rv;
	     }
    * We return the value that we test, and then set to true (side-effects)
    * Mutual exclusion using `TestAndSet`
         do {
	   while (TestAndSet(lock));
	   C-section
	   lock = false
	   remainder
	 } while (true)
    * Solution to critical section prolem : bool waiting[], bool key, lock
         do {
	   waiting[i] = true
	   key = true
	   while (waiting[i] && key)
	     key = TestAndSet(lock)
	   waiting[i] = false
	   C-section
	 } while (true)
    * Bounded waiting
          j ← (i + 1) % n
	        while (j ≠ i && ¬waiting[j])
	          j ← (j + 1) % n
	        if (j == i)
	   lock = false
	 else
	   waiting[j] ← false
	 remainder
  * Swap
         void swap(boolean &a, boolean &b) {
	       boolean temp = a;
	       a = b;
	       b = temp;
	     }
    * Mutual exclusion
          do {
	        key = true
	        while (key)
	        swap(lock, key)
	        C-section
	        lock = false
	        remainder
	      } while (true)

# Mon, February 13
* Conditional Variables
  * No value
  * Associated with a lock
  * 3 operations that can only be called in possession of lock
    1. Sleep: Release lock, go to sleep, acquire lock, return
    2. Wake: At most one thread sleeping on condition
    3. WakeAll: All threads sleeping on condition (they will all then try to
       acquire the lock)
* Classic problems of synchronization
  1. Bounded buffer problem
    * Assume pool of n buffers each capable of holding 1 item
    * Three semaphores
      * Mutex for buffer access, initialized to 1
      * Empty buffers: Initialized to n
      * Full bfyyers: Initialized to 0
    * Producer
           do {
             ... produce an item ...
             wait(empty);
             wait(mutex);
             ... add item to buffer ...
             signal(mutex);
           }
    * Consumer
           do {
             wait(full);
             wait(mutex);
             ... remove item from buffer ...
             signal(mutex);
             signal(empty);
           }
  2. Readers/writers problem
    * Assume there is a shared object among several concurrent processes
    * Two types of processes
      * Readers: Only want to read contents of shared data
      * Writers: Want to update contents of shared object
    * Two or more readers may access a shared object without any adverse effects
    * A writer must have exclusive access
    * Variations:
      * First reader-writer:
        * No reader is kept waiting unless a writer has already obtained
          permission
        * No reader should wait for other readers to finish just because
          a writer is waiting
        * Writers may starve
      * Second readers-writers:
        * Once a writer is ready, writer performs its write as soon as possible
        * If writer is waiting, no new readers may start reading
        * Readers may starve
    * Solutions using semaphores:
      * First reader-writer:
        * Mutex, write <-- 1 (semaphores)
        * readcount <-- 0 (int)
        * Reader
               {
                 wait(mutex);
                 readcount++;
                 if (readcount == 1) wait(wrt);
                 signal(mutex);
                 ... reading is performed ...
                 wait(mutex);
                 readcount--;
                 if (readcount == 0) signal(wrt);
                 signal(mutex);
               }
        * Writer
               {
                 wait(wrt);
                 ... writing is performed ...
                 signal(wrt);
               }

# Wed, February 15
* Classic problems of sychronization (continued)
  3. Dining Philosopher's Problem
    * n philosophers spend their lives thinking and eating
    * They share
      * a common table
      * n chairs
      * bowl of rice in center of the table
      * n chopsticks
    * Philosophers do not interact while eating
    * When hungry, philosophers try to pick two closest chopsticks
    * Only one chopstick picked up at a time
    * Cannot pick up chopstick if being used by someone else
    * Philosophers eat without releasing chopsticks
    * When done eating, they put down both chopsticks and start thinking again
    * Attempted solution (can result in deadlock)
               semaphore chopstick[5] (init to 1)
               do {
                 wait(chopstick[i]);
                 wait(chopstick[(i + 1) % 5]);
                 ... eat ...
                 signal(chopstick[i]);
                 signal(chopstick[(i + 1) % 5]);
                 ... think ...
               } while (true)
    * Solutions
      * At most 4 philosophers can sit simultaneously
      * Philosopher picks up chopsticks but only if both are available
      * Asymmetric solution
        * Odds pick left then right
        * Evens pick right then left
* Conditional critical region
  * when B do S (B is shared)
  * Semantcs
    1. While S is being executed no other process can access shared variables V
    2. B is a boolean expression
      * Mutual exclusion
        * Evaluated when process tries to execute S
               if (true) -> S is executed
               else sleep until no process is in S
        * How? (Using locks and conditional variables, obvi)
               Lock myLock = new Lock();
               Condition myCond = new Condition(myLock);
               myLock.acquire();
               while (!B)
                 myCondition.sleep();
               S;
               myCondition.wake();
               myLock.release();
* CPU Scheduling
  * Basis of a multiprogrammed OS
  * Basic concepts
    * Goal: Maximize CPU usage
    * Idea: Execute program until it must wait (I/O request), then take away CPU
    * Fundamental OS concept
    * Process starts
      * CPU Burst
      * I/O Wait
    * Success of scheduling depends on ability to observe cycles of CPU burst
      and I/O wait
    * Typical process
      * Starts with a CPU burst
      * Followed by I/O
      * Then alternates
    * The distribution of this process informs the best algorithm selection
    * CPU scheduling conditions
      1. Process switches from running to waiting (I/O)
      2. Process switches from running to ready (interrupt)
      3. A process switches from waiting to ready (I/O completed)
      4. A process terminates (process completed)
    * 1 & 4 form non-preemptive CPU scheduling

# Mon, February 20
* Pre-emptive/non-preemptive
  * Priority Scheduling
    * Starvation possible with low-priority processes
    * Solution: Aging --> gradually increase priority of waiting processes
  * Priority Inversion: High priority process(es) waiting for low priority one
    * What if?
      * High priority process trying to access to shared data
      * Access to shard data -> mutex, but a lower priority process has mutex
    * Solution: Priority Inheritance Protocol
      * Lower priority processes accessing a resource needed by a high priority
        process inherit the high priority until they are done with the resource
      * When finished, their priority goes back to original value
    * Mars Pathfinder
      * Embedded controllers: Watchdog timer
      * VxWorks: Preemptive priority scheduling
      * Problem: Information bus: Shared info & passing info; controlled by lock
      * Tasks: 
        B) Bus management --> Runs frequently with high priority
	M) Meteorologist --> Low priority, infrequent
	C) Communications --> Medium priority, long running
      * What happened:
        (M) got lock, (B) tries to acquire the lock --> sleep, (C) is scheduled;
	(M) and (C) in ready queue, but (M) couldn't release the lock because it
	was lower priority than (C) so couldn't run
  * Round-robin scheduling
    * Description
      * Designed for time-sharing systems
      * FCFS with preemption
      * Time slice is defined (milliseconds)
      * Ready queue is treated as a circular queue
    * Implementation
      * Ready queue is FIFO
      * New processes added to the tail
      * Scheduler picks first process in the ready queue and sets the timer to
        interrupt after one time slice
      * Two options
        1) t < t_{slice} --> process releases CPU voluntarily and schedule
	   proceeds with the next process;
	2) t > t_{slice} --> Upon timer interrupt, context switch, tail of ready
    * Average waiting time is often quite long 
    * Performance depends heavily on the value of t_{slice}
      * Comparable with context-switch overhead
      * Large t_{slice} --> FCFS
### For Exam 1, we stop after Round Robin

# Wed, February 22
* Mutlileveled Queue
  * Process permanently assigned to a queue
  * Each queue may have its own schedules
  * Must also have scheduling among queues
* Multilevel Feedback Queue Scheduling
  * Processes can move between queues
  * Main idea is to separate processes according to CPU burst times
* Windows CPU Scheduling
  * Preemptive, priority-based
  * Scheduler is called dispatcher
  * Threads selected by the dispatcher runs until of four things happen:
    1. A higher-priority thread preempts it
    2. The thread terminates
    3. T_{slice} ends
    4. Blocking system call
  * 32-level priority scheme (1 is the lowest priority)
  * Processes are divided into one of two major classes:
    * Variable class (1–15)
    * Rea-time (16–31)
  * Memory management tasks run at priority 0
  * A queue is used for each priority
    * Traversed from highest to lowest
    * No ready threads found --> Run special idle thread
  * User API (classes) (base priorities in parentheses)
    * Real-time (24)
    * High priority (13)
    * Above normal (10)
    * Normal (8)
    * Below normal (6)
    * Idle priority (4)
  * Relative priority within each class
    * Time-critical
    * Highest
    * Above Normal
    * Normal
    * Below normal
    * Lowest
    * Idle
  * If a variable class thread is interrupted because its time slice is over,
    its priority is lowered (but never before interruption)
  * When variable class thread released from wait, its priority is boosted
    * UNIX and Linux use the same strategy
    * Very good for interactive threads
    * Current window of interaction also gets a priority boost
  * For processes in the normal priority class, Windows distinguishes between a
    foreground process and background proccesses
      * Foreground process time_{slice} = 3*Background process time_{slice}

# Fri, February 24
## Second Part of Class: Memory
* Main memory
  * Concurrently running processes share memory
  * Overview:
    * Memory: Large array of words/bytes, each with an address
    * Instruction cycle:
      * Instruction fetched from _memory_
      * Instruction decoded --> May cause operands to be teched from memory
      * Once executed, results may be stored in memory
    * Memory Unit: Only sees memory addresses; does not know encoding
    * Address Binding
      * Program stored in secondary storage as an executable
      * Process may reside in any prat of physical memory (when loaded)
        * Address space of a computer starts at 0000
	* User process may reside anywhere
      * Addresses in a source program are symbolic (count, j, result)
      * A compiler binds symbolic addresses to relocatable address
  * Linker/loader binds relocatable addresses to absolute addresses
  * Bindings occur at the different times throughout the life of a process/gram
  * Source -> Compiler -> Object -> Linker -> Loader -> Memory
    * Linker: Other object code
    * Loader: System libraries
    * Memory: Dynamic Link Loader
  * Notes
    * Compile Time: If process location known ahead of time -> Absolute Address
      * Windows' ".com" executables are the only examples Dr. Papa knows
    * Execution Time: If process can be moved from one memory segment to another
      during execution, then its binding is delayed until runtime
  * Logical vs. Physical Address space
    * Logical address: Generated by CPU
    * Physical address: Generated by memory address
    * CPU: Logical address -> relocation register (in mmeory unit) -> Memory
  * Dynamic Loading
    * Entire program must be loaded in memory to execute
      * Size of process is limited to size of physical memory that is available
    * Solution
      * Routines/functions/methods are not loaded in memory until called
  * Dynamic linking/shared libraries
    * Some OSes only support static linking
    * Some libraries treated like any other object module and combined by loader
      into a binary image
    * Similar to dynamic loading
      * Difference: Linking is postponed until run-time
    * Stub included by linker
      * It checks if routine is loaded in memory; if not, loads it
      * Next time it's needed, it's already in memory! YAY ONE COPY :)
    * It needs help from OS
        * Memory has to be protected
	* May need to allow multiple access [reentrant code]
  * Contiguous memory allocation
    * Memory is divided into partitions
    * Each process contained in a simple contiguous section of memory
    * Memory unit with memory protection
      * If the program should not access the memory it's requesting, terminate
        it with segfault/abort trap

# Mon, February 27
* Memory allocation (continued)
  * One of the simplest methods
    * Divide memory into several fixed-sized partitions
    * Each partition may contain exactly one process
  * Originally used IBM OS/360
  * ReguH: Holes in memory (scattered)
  * Dynamic storage allocation problem
    * First fit: Allocate first hole big enough (limited search)
    * Best fit: Allocate smallest hole that's big enough
    * Worst fit: Allocate largest hole
    * Simulation results
      * First fit & best fit are better than worst fit
      * First fit is a little faster
      * First fit: About 1/3 of total memory goes unallocated; 50% rule (for
        every two blocks that are allocated, one block remains empty)
    * External fragmentation: Enough total memory exists to satisfy an
      allocation request but it is not contiguous
    * Compaction: A solution to the external fragmentation problem
    * Paging: Non-contiguous memory allocation; supported by hardware
      * Basic method: Break physical memory into single-size frames
      * Logical memory broken into pages (which are the same size as the frames)
      * Page size: Typically a power of 2
      * CPU gets the page number and logical address, looks up the page
        number to get the frame number, and calculates the physical address.

# Wed, March 1
* Paging (continued)
  * Wastes about 1/3 of memory
  * Goal: No external fragmentation
  * Internal fragmentation (possible)
    * Unused portion of a frame
	* Mapping concealed from user
	* OS must keep track of all frames
	  * Free frames
	  * Used frames [owner]
* Segmentation
  * Users prefer to view memory as a collection of variable-sized segments
    where each segment represents a different part or component of a program
  * Logical address space => Collection of segments
    * Each segment will have a name (a number) and a length
	* Logical address is a tuple (segment #, offest)
  * Problem: Segments are allocated contiguously
  * Solution: Segmentation with paging
    * Each segment has a page table --> Segment is allocated non-contiguously
##Chapter 9: Virtual Memory
* Allows execution of processes that may not be completely in memory
  * Programs can be larger than physical memory
  * Benefits:
    * Program no longer constrained to physical memory
	* More programs running at the same time (increased multiprogramming)
  * Transparent to user (handled by OS)
  * How? Use secondary storage as main memory extension
  * Implementation:
    * Demand paging (load pages into physical memory as needed)
  * Steps
    1. Check internal page table and determine whether reference is valid
	  * If invalid, terminate processs
	  * If valid, load the page into memory
	2. If page is loaded, access memory
	3. Else, page it in (find a free frame and schedule the page to be read)
	4. Continue instruction execution

# Fri, March 3
## Exam Review
* Ch1: Intro
  * What is an OS?
  * Types
  * Computer operation: Interrupts/traps
  * DMA
  * Main memory
  * VERY IMPORTANT: Hardware protection (the day that I missed)
    1. CPU
	2. Memory
	3. IO
  * Dual-mode of operation
* Ch2: OS
  * Services for programs
  * System calls
  * Communications
* Ch3: Processes
  * Concept
  * Context-switch
    * Stale
  * Threads
  * Operations
    * Creation: Fork, exec
	* Terminate: Exit, abort
  * Cooperating processes
    * Producer-consumer
  * Interprocess communication
    * Direct/indirect (message passing, mailbox)
	* Symmetric/asymmetric
  * Synchronization (send/receive)
    * Blocking/non-blocking
* Ch4: Threads
  * Benefits/concepts
  * Kernel/User threads
  * MODELS: Many-to-one, one-to-one, many-to-many
  * Issues: _Fork_ and exec
  * Signal handling
  * Tutorial/Java threads
* Ch6: Process synchronization
  * Race condition
  * Critical section problem
    * Properties of any solution
	* Algorithms
  * Synchronization primitives (atomic operations)
    * Lock
	* testAndSet
	* Swap
    * Semaphores
	  * Different types of synch problems
	  * Busy vs. non-busy waiting (Nachos)
    * Deadlock & startvation
	* Classic synch problem
	  * Bounded buffer (semaphores)
	  * Readers-writers (1st & 2nd)
	  * Dining philosophers
	* Condition variables
	  * Definition
	  * Compare to locks and semaphores
	* Conditional critical region
	  * How to implement using condition variables
* Ch5: CPU Scheduling
  * Goal: Maximize CPU business
  * IO/CPU burst
  * CPU scheduling & and the scheduler
  * Conditions for preemptive vs non-preemptive
  * Scheduling criteria
  * Scheduling algorithms
    * FCFS (non-preemptive)
	* SJF (Shortest job first)
	  * Could be preemptive or non-preemptive
	* Priority (preemptive, non-preemptive)
	  * Priority inversion
    * Round-robin

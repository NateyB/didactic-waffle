# Mon, January 9
* OS
  1. Program that manages computer hardware
  2. Provides basis for applications and programs
  3. Intermediary between user and hardware
* Diverse in accomplishing tasks
  * Maximize hardware utilization → Optimize hardware utilization
  * PC OS: Support complex games, business apps, and anything in-between
  * Some OSes designed for efficiency, others for convenience, etc.
* Computing Systems
  * 4 major components: Users, apps/compilers/etc., OS, Hardware
  * "OS is similar to government"
  * Different points of view:
    * User view: Varies according to interface
	  * PC OS: Originally designed for one user to monopolize resources;
	    Goal: Ease of use
	  * Mainframe/Clusters/Supercomputers: Maximize resource utilization; user
	    sits at a terminal; fair task scheduling, etc.
	  * Mobile Devices: More limited computer; energy efficiency, etc.
    * System view:
	  * OS is the program most involved with the hardware → resource allocator
	    * Resources: CPU time, memory space, file storage/space
		  * OS acts as a manager: May face conflicting requests
		  * Resource allocation is _very important_
	  * Defining OS: No universally accepted definition
	    * The one piece of software for controlling and operating a computer
		  system
		* Simple viewpoint: Everything that the vendor ships when you order one
		* The one program running at all times (the **kernel**)
* Computer organization
  * Powerup: Need initial program to run (_bootstrap program_)
    * Initializes registers, device controllers, memory
	* Must be local & loads OS into memory
	* OS starts executing first process (init) and waits for _events_
	  * Events are _signaled_ by an _interrupt_
	    (hardware/software: system call/trap)
	  * Most OSes are _interrupt driven_, but the alternative is _polling_,
	    which is horrible
	  * When the CPU is interrupted, it stops what it's doing & transfers
	    execution to a fixed location, which contains the starting address of
		the _service routine_
	  * Interrupt vector: A vector of interrupts' starting addresses
	  * Interrupts must be serviced quickly and the start of the CPU must
	    be saved
	  * Interrupts may have priorities (interrupt masking)

# Wed, January 11
* Computer organization (continued)
  * Storage structure
    * Programs in RAM: Only large storage area CPU can access
	* CPU accesses memory through load/store (memory <-> register)
  * Typical instruction execution
    * Fetch instruction from memory → instruction register
	* Decode instruction; may have to get stuff from memory
	* Execute instruction: Result may have to be stored in memory
  * Memory is very important
    * Memory unit only sees addresses: Does not know their origin or genesis
	* Programs/data may not reside in main memory
	  * Main memory is too small to store all data & programs
	  * Main memroy is volatile
	* Therefore, secondary storage
	  * Primary requirement: Hold large quantities of data permanently
  * I/O Structure
    * Many types of IO devices (memory among them)
	* CPU and device controllers connected through a common bus
	* Operation:
	  * Start IO: Loading registers in device controllers; device examines
	    register and peforms action
	  * Transfer data
	  * Upon completion, interrupt
	  * Problem: This method could cause large overhead when moving large
	    blocks of data
	  * Solution: Direct Memory Access
* Computer System Architectures: Classified by # of processors and how used
  * Simple processor: One CPU (executing instructions)
  * Multiprocessor systems:
    1. Throughput: Speedup by ratio slightly less than N processors
	2. Economy at scale: Ability to share peripherals
	3. Reliability: One can fail, the system does not shut down
	* SMP: Symmetric multiprocessing (Windows, Linux, macOS)
	  * Each processor runs identical copies of the OS
	  * Processors communicate as needed
	* AMP: Asymmetric multiprocessing (Sun OS v4)
	  * Each processor has a specific task
	* Clustered system: Supercomputers/HPC
	  * Individual systems coupled together & closely linked through LAN

# Fri, January 13
* OS Structure
  * Provide environment within which programs execute
  * Share common features
    * Multiprogramming: Increases CPU utilization by organizing jobs so that the
	  CPU always has time to execute—otherwise, it would be very difficult to
	  maximize CPU usage
	  * Multiprogrammed systems: 1960s–Present
	    * Job scheduling allows multiprogramming
		  * Ideas
		    * OS keeps several jobs in memory
			* OS picks one & starts execution
			  * If a job waits for IO, then CPU switches to another job
			  * Eventually, job finishes waiting and interrupts the CPU to
			    regain control
		  * CPU never idle as long as there is a job
		  * First time the OS has to make decisions for the user
		    * Fairly sophisticated
			* CPU scheduling
			* Safe environment: Processes should not be able to affect others
			  negatively
	  * Time sharing systems: 1970s–Present (UNIX)
	    * Logical extension of multiprogramming
		* CPU switches jobs so frequently so that users can interract with
		  programs while they run
		* User interacts with system (keyboard, screen)
		* User gives commands and expects short response times
		* If all jobs are switched fast enough, each user is given the
		  impression that the system is dedicated to their use
* OS Interface
  * Command interpreter (shell)
    * User enters command manually
	* Bourne, C, Bourne Again SHell, Kain Shell
	* All provide similar functionality
  * GUI
    * Desktop metahore (user-friendly)
	* Keyboard, mouse, touchscreen
	* From research facility XEROX PARC in the 1970s (they created ethernet,
	  OOP, "great great things")
	* Apple Macintosh first one in 1980
	* Windows V1–1985
	  * Extended MS-DOS prompt; legal challenges from Apple
	* Windwos V2 (1987), V3 (1990), and many others after that
  * Communication
    * Message passing (A sends to OS, OS sends to B)
	* Shared memory (A sends to shared, shared sends/is read by B)

# Wed, January 18
* Timesharing (continued)
  * OS makes sure timer is set to interrupt
  * Clearly instructions to modify timer are privileged
  * We can control how long a program can run
  * Program calls yield (non-preemptive)
  * Timer interrupts (preemptive)
  * Transfer control to OS (privileged mode)
    * OS prepares system for next job (context-switch)
  * I/O Protection → [I/O Instructions privileged]
  * Memory protection: Memory unit will check whether address is "valid"
* OS components/structures
  * Process management (process control)
    * Process → Instance of a program in execution
  * Create/Delete (user/system processes)
  * Suspending/resuming processes
  * Process synchronization (semaphore)
  * Process communication
  * Avoid deadlock (wait event, signal event)
  * Main memory management
    * Main memory → Central to operations of a computing system
	* Repository of quickly accessible data
	* Must keep more than one program in memory
	* Functions
	  * Keep track of which portions of memory are being used by which programs
	  * Deciding which process to load in memory when space is available
	  * Allocating or deallocating space as needed


# Fri, January 20
## Processes: Chapter 3
* Process: Instance of program in execution; a unit of work
  * All the processes can run concurrently
  * Parts: More than program code; also include
    * Program counter
	* Process stack (temporary data, method parameters, local variables)
	* Data section of a program
* Process state: Processes change state while executing:
  * new --admitted→ ready
  * ready --scheduled/dispatched→ running
  * running
    * --exit→ terminated
    * --I/O or wait for event→ waiting
	* --interrupt→ ready
  * waiting --I/O or event completion→ ready
* One process running/CPU, but many ready/waiting
* PCB: Process control block; representation of a process in OS
  * Pointer; state; number; program counter; registers; memory limits; other
    resources (like files)
* Switching processes: A context switch saves the current process and
  loads the next process
* Process scheduling
  * Schedules for CPU & IO
  * CPU scheduler: Must be fast

# Mon, January 23
* UNIX
  * Exact duplicate of processes can be made with the fork() command,
  and both the parent and child processes continue after the fork,
  which for the parent returns the pid of the child and returns 0
  for the child
* Windows
  * There is another option, which allows a new program to be loaded to
  the process' memory. This call is execlp(); only Windows supports both
* Process termination
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
          * It's okay for producer to make an item while consumer is using a
		    different one
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
          * Fixed-length messages → Easy to implement, more difficult to use
          * Variable-length → Difficult to implement, more powerful
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
      * Supported directly by kernel (kernel → thread creation/execution
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

# Fri, February 3
## Chapter 6: Process Synchronization
* Example: Problem/issue with concurrent execution of counter++ and counter--
  in the producer-consumer problem
* Concurrent execution of statements (high-level language) is equivalent to
  a sequential execution of lower-level instructions (could be interweaved
  in an arbitrary order)
* Race condition: Outcome of execution depends on the particular order in
  which access to shared resources takes place
  * Solution: Ensure that only one process at a time can be manipulating
    critical (shared) variables
* Critical Section Problem
  * Consider n process {P_0, P\_1, ..., P_{n - 1}}
  * Each process has a segment of code called its critical section in which
    shared resources are being accessed
  * When one process is executing its critical section, no other process is
    allowed to execute its critical section; they are mutually exlusive in time

# Mon, February 6
* Critical (C-)section problem
  * Design a protocol that processes use to cooperate
    * Each process must request permission to enter its critical section
    * Permission request done in entry section
    * Critical section may be followed by an exit section
    * Remaining code → Remainder section
  * Typical process:
        Do {
          entry section
          critical section
          exit section
          remainder section
        } while (true)
  * Any solution must satisfy these three requirements
    1. Mutual exclusion: If P_i executing C-section, no other P_j is (i ≠ j)
    2. Progress: If no process is executing its C-section, and some processes
       wish to enter their critical sections, then only those processes
       **not** executing in their remainder section can participate in the
       decision and the selection cannot be postponed indefinitely
    3. Bounded waiting: There exists a bound on the number of times that other
       processes are allowed to enter their C-section after a process has made
       a request and before the request is granted
  * 2-Process Solutions (Proposed) (These algorithms are lacking detail):
    1. First solution (the turn variable is shared):
           do {
             while (turn ≠ i);
             critical_section
             turn ← j
             remainder_section
           } while (true)
      * Mutual exclusion: Satisfied
      * Progress: Not satisfied
    2. Boolean flag[2]
           do {
             flag[i] ← true
             while flag[j];
             C-section
             flag[i] ← false
             remainder
           } while (true)
      * Mutual exclusion: Satisfied
      * Progress: Not satisfied
    3. Peterson's Solution (two processes) (turn, flag[])
           do {
             flag[i] ← true
             turn ← j
             while (flag[j] && turn == j);
             C-section
             flag[i] ← false
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
	       boolean rv ← target;
	       target ← true;
	       return rv;
	     }
    * We return the value that we test, and then set to true (side-effects)
    * Mutual exclusion using `TestAndSet`
          do {
	        while (TestAndSet(lock));
	        C-section
	        lock ← false
	        remainder
	      } while (true)
    * Solution to critical section prolem:
	      bool waiting[], bool key, lock
          do {
	        waiting[i] ← true
	        key ← true
	        while (waiting[i] && key)
	          key ← TestAndSet(lock)
	        waiting[i] ← false
	        C-section
	      } while (true)
    * Bounded waiting
          j ← (i + 1) % n
	      while (j ≠ i && ¬waiting[j])
	        j ← (j + 1) % n
	      if (j == i)
	        lock ← false
	      else
	        waiting[j] ← false
	      remainder
  * Swap
         void swap(boolean &a, boolean &b) {
	       boolean temp ← a;
	       a ← b;
	       b ← temp;
	     }
    * Mutual exclusion
          do {
	        key ← true
	        while (key)
	        swap(lock, key)
	        C-section
	        lock ← false
	        remainder
	      } while (true)

# Fri, February 10
* Synchronization primitives
  * Semaphores: Have an integer value, and con only be accessed through two
    atomic operations:
	* Wait (P): `wait(S) { while (S <= 0); S--; }` (bad because busy-watiting)
	* Signal (V): `signal(S) { S++; }`
	* Typically, but not necessarily, initialized to 1
	* Mutual exclusion (S ← 1): `do {wait(S); C-section signal(S) remainder}
	  while (true);`
* Synchronization problem
  * P1, P2 concurrent with statements S1, S2, respectively. Execute S2 only
    after S1.
  * Solution: P1, P2 share a semaphore ← 0; `P1: S1 signal(synch); P2
    wait(synch) S2;`
  * A deadlock affects a _set_ of threads
  * Starvation, however, is only one process (waiting indefinitely within a
    synch primitive)
* Condition variable
  * Synch primitive without a value, but threads may still be queued on them
  * Associated with a lock
  * All operations can only be used when holding the lock
  * Operations:
    * Sleep: Release the lock and sleep on this condition variable until some
	  other thread wakes it; acquire the lock before returning from sleep.
	  * Semaphore: Wait only sleeps thread if value is nonpositive;
	  * Lock: Acquire only sleeps if the lock is busy
	* Wake: Wakes at most one thread sleeping on the lock
	* WakeAll: Wakes all the threads sleeping on the condition variable

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
      * Full buffers: Initialized to 0
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
        * Mutex, write ← 1 (semaphores)
        * readcount ← 0 (int)
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
  * Semantics
    1. While S is being executed no other process can access shared variables V
    2. B is a boolean expression
      * Mutual exclusion
        * Evaluated when process tries to execute S
               if (true) -> S is executed
               else sleep until no process is in S
        * How? (Using locks and conditional variables, obvi)
               Lock myLock ← new Lock();
               Condition myCond ← new Condition(myLock);
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

# Fri, February 17
## Chapter 5: CPU Scheduling
* Scheduling criteria
  * Maximize
    * CPU Utilization: 0–100
	* Throughput (measure of work): #processes/time
  * Minimize
    * Turnaround time: Time from submission to time of completion
	  * Sum of (wait to load) + (wait to be ready) + (execution time) + (IO)
	* Waiting time: Time waiting in ready queue
	* Response time: Time from time of submission to time of first response
* Scheduling algorithms
  * Consider 1 CPU burst/process for simplicity
  * First-come first-served (FCFS): Exactly what it sounds like. Non-preemptive
  * Shortest Job First (SJF)
    * FCFS used to break ties
	* Optimal (minimum average waiting time for a set of processes)
	* Problem: Determining the next CPU burst is impossible
	* Solution: Approximate burst time
	  * T_{n + 1} ← a\*t_n + (1 - a)*T_n; T ← predicted, t ← actual
	* Preemptive version: Shortest remaining time first (based on arrival)
  * Priority scheduling
    * SJF is a special case of priority scheduling
	* A priority is assigned to each process
	* CPU is allocated to process with highest priority
	* Equal priority processes are scheduled in FCFS fashion

# Mon, February 20
* Pre-emptive/non-preemptive
  * Priority Scheduling
    * Starvation possible with low-priority processes
    * Solution: Aging → gradually increase priority of waiting processes
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
        B) Bus management → Runs frequently with high priority
	M) Meteorologist → Low priority, infrequent
	C) Communications → Medium priority, long running
      * What happened:
        (M) got lock, (B) tries to acquire the lock → sleep, (C) is scheduled;
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
        1. t < t\_{slice} → process releases CPU voluntarily and schedule
	       proceeds with the next process
	    2. t > t_{slice} → Upon timer interrupt, context switch, tail of ready
    * Average waiting time is often quite long
    * Performance depends heavily on the value of t_{slice}
      * Comparable with context-switch overhead
      * Large t_{slice} → FCFS

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
    * Real-time (16–31)
  * Memory management tasks run at priority 0
  * A queue is used for each priority
    * Traversed from highest to lowest
    * No ready threads found → Run special idle thread
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
      * Foreground process time_{slice} ← 3*Background process time_{slice}

# Fri, February 24
## Second Part of Class: Memory
* Main memory
  * Concurrently running processes share memory
  * Overview:
    * Memory: Large array of words/bytes, each with an address
    * Instruction cycle:
      * Instruction fetched from _memory_
      * Instruction decoded → May cause operands to be teched from memory
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
    * CPU: Logical address -> relocation register (in memory unit) -> Memory
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
  * Logical address space → Collection of segments
    * Each segment will have a name (a number) and a length
	* Logical address is a tuple (segment #, offest)
  * Problem: Segments are allocated contiguously
  * Solution: Segmentation with paging
    * Each segment has a page table → Segment is allocated non-contiguously
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

# Friday, March 10
* Evaluating page replacement
  * Use "reference strings" generated randomly (represent access ops)
  * For a given page size, we need only to consider the page number
  * If we have a reference to page p, then any immediately following references
    to p will never cause a page fault
  * FIFO Page replacement
    * FIFO algorithm
	  * Associate/keep track of "time" when page was loaded into memory
	  * When a page must be replaced, the oldest one is chosen
	  * % of page replacement: (#pages needing replacement)/(#pages loaded)
  * Belady's Anomaly: Page fault rate may increase as number of allocated frames
    increases

# Monday, March 20
* Page replacement (cont.)
  * OPT: Optimal Page Repacement (MIN):
    * Replace the page that will not be used for the longest amount of time
	* His algorithm for doing this makes no sense. He iterates through each
      possible page to replace, going until the next occurence of that page
	  to find the page that will not be used for the longest; he should just
	  iterate through each, checking if it is a page replacement, and then stop
	  when he's found instances for all but 1 of the candidate pages to replace
	  the candidate page that he has not found an instance for
	* Difficult to implement (requires knowledge about the future)
	* Good to use for comparison with other algorithms
	* You can also try to approximate it
	  * LRU page replacement (Least Recently Used) (trying to approximate OPT)
	    * Considered good, often used
  * Summary
    * FIFO: Replace page that's been in table for the longest time
	* OPT: Replace page that will not be used for the longest time
	* LRU: Based on past history, replace the page that has not been used for
      the longest time
* Allocation of frames
  * How do we allocate free memory among various processes?
  * Minimum # of pages
    * We know we cannot allocate more than total number of frames
	* We must allocate at least a minimum number of frames
	* Minimum # determined by instruction set
	  * All pages used by instruction causing page fault must be in memory
	  * Worst case: Computer architectures with multiple levels of indirection
	    * PDP-11: Move operation that required 6 frames
		* IBM-370: Move instruction that may require 8 frames
  * Summary
    * Minimum: Determined by the architecture of the CPU that we're using
	* Maximum: Determined by the amount of physical memory that we have
	* The number of frames that we allocate is somewhere between these two
	  * Many algorithms in between
  * Equal allocation
    * m = # of frames
	* n = # of processes
	* We allocate m/n frames/process
  * Proportional allocation
    * S\_i = size of process/size of all processes, S
	* We allocate S\_i/S*m frames for process i


# Monday, March 27
* Filesystems (cont.)
  * Minimal set of 6 operations
    * Renaming, copying, moving, appending (notably not 6...)
  * Most operations require search in directory (takes time)
    * OS keeps open file tables:
	  * Preprocess table
	  * System wide table: Count (increased with open, decreased with close)
	  * Several processes may open the same file
  * File types
    * Support of file types help avoid problems
	  * Printing a binary file
	* May be done by adding extensions to the filename (hints; not strong)
	* In UNIX, some files include a "magic number" int indicating type of file
  * File structure
    * Certain files _must_ conform to a certain structure (i.e., executables)
	* Disadvantage
	  * Support for too many file structures makes OS cumbersome
	* Problem: How about applications requiring files not structured in ways
	  supported by OS?
	  * Txt and exe files are not really appropriate for encrypted files
	* OSes support a minimal set of file structures
	  * All OSes support at least one file structure: Executable
    * Internal file structure
	  * Disk I/O operations are performed in units of one one-sized block
	  * Logical record length != physical record length
	  * Logical -> physical = packing
	  * Physical -> logical = unpacking
	  * UNIX: Each byte in a file is individually addressable by its offset
	    from the beginning of the file
	    * Logical record length is one byte
		* But the OS/File system packing & unpacking into physical blocks
		  (transparent to the user)
	  * Summary:
		* Files may be considered to be a sequence of blocks
		* All I/O operations (on record storage) done in blocks
		* Internal fragmentation is possible in the last block of file
  * Access Methods
    * Sequential access
	  * Information is processed in order, one record after another
	  * Most common (editors, compilers)
	  * File pointer to keep track of position
	  * Based on "tape model of a file" (think of machines)
	* Direct access
	  * Files constituted by fixed-length logical records
	  * Based on the "disk model of a file"
	  * Provides immediate access to information
	  * Used by databases
	* It is easier to simulate sequential access having a direct access device
  * Directory Structure
    * Disk may store large amounts of files (we need to organize them)
	* Organization (2 parts)
	  * Arrange partitions (Split drive, merge)
	  * Information about files within partition stored in a deevice directory
	    (another directory) or volume table of contents
	* Directory: A symbol table translating file names into their directory
	  entries
	* Operations on a directory
	  * Search for a file
	  * Create
	  * Delete
	  * List
	  * Rename
	  * Traverse file system

# Wednesday, March 29
* **Test 2 Content:** Everything after Test 1, before filesystems
* Directories (cont.)
* Single level directory
  * Simplest structure: All files in the same directory (easy to understand
	and implement)
  * Directory names have limits:
	* MS-DOS: 11 characters (8 name + 3 extension)
	* UNIX: 255 characters
  * Not very practical for common use
	* No organization
  * Confusing if multiple users storing files
  * May run out of names?
* Two-level directory
  * Create a separate one-level directory for each user
  * Two or more users can have files with same name
  * Special entry for OS/System
  * Need syntax to access files
	* May be different for different OSes (C:\ vs /)
  * Still need more to help organize files
* Tree-structured directory
  * Generalization; absolute/relative paths are the same
* DAG directories (UNIX, Windows)
  * Sharing directories (what to do?)
	* UNIX: Link -> Pointer to another file or directory
	* Issue: Problem is that cycles are allowed, so filepaths not
	  necessarily unique
	* UNIX: Hard links & soft links; Windows: Soft links only
	* Hard link: Permission changes on one directory is immediately
	  refected on the other directories to which that one is hard-linked
	  * Keep count of references; the count is decremented when the
		entry is deleted (deallocating when the count hits 0)
	  * Must reside in the same file system
	* Soft link:
	  * Type of file: Contents are a path to another directory
	  * Removing original results in an orphan link
	* Directories (UNIX):
	  * Initially populated with 2 files
		* . & ..
		* These are hard links

# Monday, April 3
Chapter 11: File system implementation
* File system structure
  * Disks: Bulk of secondary storage (where filesystem is maintained)
    * Rewritable in place: Read/write/modify same block
	* Access to any block: Simple to use for sequential and direct access
	* I/O transfers are by blocks (4096 bytes)
  * OS imposes a file system on the disk
    * Design problems: How does the file look ot users; algorithms (such as
	  the logical/physical mapping)
	* Generally layered
	  * Devices .................. disks
	  * I/O Control .............. device drivers (retrieve block 123)
	  * Basic file system ........ generic commands (read/write physical block)
	  * File organization module . Knows about files and their logical blocks
	  * Logical file system ...... Manages file metadata; directories; file
	    structure (FCB---file control block) such as ownership, permissions,
		location, etc.
	* Many different types of filesystems
	  * UNIX/Linux: UFS, ext3, ext4
	  * Windows: FAT32, NTFS
	  * CD/DVD [read-only]
	* Overview: Several on-disk/in-memory data structures (implementation)
		* On-disk:
		  1. Boot control block
			* Info to boot from that partition (empty if not bootable)
			* Typically first block of partition: UNIX --> boot block, NTFS -->
			partition boot sector
		  2. Partition control block
			* Partition details (# of blocks, size of block, free-block
			  pointers, FCBs)
			* UNIX: Super block
			* Windows/NTFS: Master file table
		  3. Directory structure
		  4. FCBs
			UNIX --> inode (UFS/ext3/4), NTFS: Master file table (relational DB)
		* In-memory
		  1. In-memory partition
		  2. In-memory directory structure (recently accessed directories)
		  3. System-wide open-file table
		  4. An open-file table for every process
		* File creation (system call)
		  * Allocate a new FCB
		  * Read directory into memory, update, and write back
		* File opening (system call)
		  * Search directory structure
		  * Copy FCB to system-wide open-file table
		  * Make an entry in per-process open-file table
		  * Update other fields: Current read/write location
		  * Return a reference (only in per-process open-file table)
		    * Filename is useless once FCB is located
			* UNIX --> File descriptor; Widnows --> File handle
		* Directory implementation
		  * Linear list
		    * Linear list of file names with pointers to data blocks
			* Requires linear search to find entry (simple, but time-consuming)
			  * Create: Search, ensure no file has same name, add to end
			  * Delete: Search, release space if found
			  * Reuse (optimization): Mark entries unused
		    * May use a linked-list to decrease time it takes to delete/add a
			  file
			* Real disadvantage: Linear search to find a file
			  * Some systems cache
			  * Sorted list: Binary search
			    * Complicates process of creating/deleting a file
		  * Hash table

# Monday, April 10
            * Uses hashing fuction with filename
			* Advantage: Decreased search time
			* Issues: Collisions, and changing the table size
  * Allocation methods (efficient space and fast access)
    1. Contiguous
	  * Each file occupies contiguous storage blocks on storage device
	  * Secondary storage device defines linear ordering
	  * Allocation define by address (starting block) and length (in blocks)
	  * A file n blocks in size starting at block b uses blocks b..b + (n - 1)
	  * Directory entry includes the starting address and the length
	  * Access
	    * Access is simple
		* Sequential and direct access
	  * Problems
	    * Finding space for a file (first fit, best fit, worst fit)
		* External fragmentation
	2. Linked allocation
	  * Solves problems of contiguous allocation
	  * File is a linked list of blocks
	  * Directory entry contains pointers to first and last blocks
	  * Access
	    * Write --> If we need to add, find block & update pointers
	  * Pros
	    * No external fragmentation
		* Do no need to know the size of the file when created
		* Files can grow as long as there is space
		* No need for compaction
	  * Cons
	    * Not effective for direct access (must follow pointers)
		* Space required for pointers
		  * 4-byte pointers in a 512-byte block is ~0.8%
		* Reliability (if a pointer is lost, we're in big trouble)
	  * File allocation table (a very important) variation of linked allocation
	    * Section at beginning of disk contains a table (a map of the drive)
		* Table has one entry/block
		* Unused blocks marked as null
	3. Indexed
	  * Solves the problems of linked allocation
	  * Store all pointers in one block---the index block
	  * Directory entry points to index block
	  * ith entry in the index block points to the ith block of the file
	  * Access
	    * When file is created, allocate index block & fill with nulls
		* When ith block is first written, a faree block is allocated and its
		  address stored in the index block
	  * Pros
	    * Supports direct access (no need to chase pointers on disk)
	  * Cons
	    * May be wasting space (two blocks required per file minimum)
	  * How big should index block be?

# Wednesday, April 12
      * Index block size (cont.)
	    * If too small, not enough entries for large files
		* If too big, wasted space
		* As small as possible while accomodating large files
		* Solutions:
		  1. Linked scheme: Last entry in index block points to next index block
		  2. Multilevel index: First index block points to a set of index blocks
		  3. (UNIX) Combined scheme [inode]:
		    * Direct blocks [12?]
			* Single indirect
			* Double indirect
			* Triple indirect

# Monday, April 17
* RAID (Redundant Array of Indepent Disks) Arrays
  * RAID 0: "Striping"
    * n disks (minimum of 2)
	* space efficiency: n
	* Fast
	* Disk failure/error => entire array is lost
  * RAID 1: "Mirroring"
    * n disks that are the same
	* High fault tolerance: Array operates as long as there is one drive
  * RAID 2: "Hamming Code Parity"
    * Bit striping with error codes
	* Minimum of 3 disks
	* Parity bits in the third disk
	* Won't be used (most disks have parity bits)
  * RAID 3: Byte-level striping
    * Space: n - 1 (one dedicated parity disk)
	* Minimum # of disks: 3
	* Improved performance/fault tolerance
	* Parity => bottleneck for concurrent operations
	* If parity drive fails, the array still works
  * RAID 4: Block-level striping, dedicated parity
    * Parity drive may also be a bottleneck
  * RAID 5: Striped set with distributed parity
    * 3 disk minimum
	* n - 1 efficiency
	* A1 A2 A3 Ap; B1 B2 Bp B4; C1 Cp C2 C3; Dp D1 D2 D3
	* All drives but one needed to operate
  * RAID 6: Striped set with dual distributed parity
    * 4 disk minimum
	* Efficiency: n - 2
	* Up to two drives fail, and still operate

# Monday, April 24
* Exam review
  * Chapter 1: Intro to operating systems & computer organization
    * What's an OS? (Different definitions)
    * Types
	* Computer operations: Bootstrap loads OS, interrupts/traps
	* Components: Main memory, direct memory access, dual-mode of operation
	* Proteection: CPU, memory, I/O
  * Chapter 2: OS Structures
    * Management: Processes, main memory, etc.
	* Services for programs: Program execution, files, protection
	* System calls: Concept
  * Chapter 3: Processes
    * Concept/PCB (Process control block): Context switch, threads, scheduling
	* Operations on processes: Creation, execution, (fork & join), termination
	* Cooperating processes: Producer-consumer problem
	* Interprocess communication: Message passing, (in)direct and (a)symmetric
	* Synchronization/communication: Non-blocking send/receive, buffering
  * Chapter 4: Threads
    * Overview/benefits, user vs kernel, and modes (1:1, m:1, m:n)
	* Cancellation (asynchronous/deferred)
	* Signal handling
	* Threads tutorial (Java threads)
  * Chapter 6: Process synchronization
    * RACE conditions
	* Critical section problem and solution: Mutual exclusion, bounded waiting,
	  and progress
	* Algorithms; primitives (lock, testAndSet, swap, semaphores)
	* Classic problems of synchronization
	* Condition variables, conditional critical region
  * Chapter 5: CPU scheduling
    * Goal: Maximize CPU usage (CPU & IO bursts)
	* CPU Scheduler: Preemptive vs non-preemptive (4 conditions)
	* Scheduling criteria
	* Algorithms
	  * FIFO (non-preemptive)
	  * SJF (both)
	  * Priority (both); priority inversion
	  * Round robin
	  * Multilevel feedback queue
	  * Windows' "dispatcher"
  * Chapter 8: Memory management
    * Instruction cycle
	* Address binding
	* Optimization: Dynamic loading & dynamic linking
	* Memory allocation
	  * Contiguous: External fragmentation
	  * Noncontiguous: Paging, segmentation, segmentation with paging
  * Chapter 9: Virtual memory
    * Good things; effective access time
	* Overallocation: Page replacement (FIFO, OPT, LRU)
	* Frame allocation: Min, max; Equal or proportional allocation
  * Chapter 10: File system interface
    * What is it?
	* Files; file tables: System-wide and per-process; access methods
	* Directory structures: Single-level, duual-level, trees, DAGs
	* File permissions
  * Chapter 11: File system implementation
    * Layered; in-disk or in-memory
	* Operations: File creation, access, opening, deletion
	* Directory implementation: Linear list, hash table
	* Allocation methods: Contiguous, linked list, file allocation table, index
	  * Multilevel index
	  * A combination: inode, UNIX


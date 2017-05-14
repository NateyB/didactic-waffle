# didactic-waffle
CS 3053—Operating Systems

Only the files changed from the Nachos download will be added to the repo.
Other than that, the rest of the files are unchanged from the Nachos download
provided to me on Harvey.

Here is an overview of the projects that I did:

## Project 1: Kernel Thread System
This project required me to implement some basic thread synchronization
utilities for Nachos.


#### Task 1: Thread Joining
I implemented thread joining; this was achieved by using a thread queue
(provided by the current scheduler) to store the reference in the joined
thread. Then I simply put the current thread to sleep and waited until the
joined thread finished, then rewoke it.

#### Task 2: Condition Variables
This implementation was simple. Any thread that called sleep was added to a list
and released its lock. Then it slept. When it woke, it reacquired the lock.
When wake was called, a thread sleeping in the list was popped and readied.

#### Task 3: Alarms
I implemented my alarm class by creating a map from threads to wake times. Then,
when a timer interrupt occurred, I iterated through the map, waking threads
whose wake times were greater than the current time.

#### Task 4: Communicators
This task required implementing mailboxes (or 'communicators'). This enabled
threads to communicate. I simply used a shared lock between two condition
variables: Listening & speaking. Speaking simply slept the thread on the
sleeping condition if there were no available listeners. Then the word was set
and the listener were woken. The listening mode incremented the count of
listeners (so that the speakers could know if they could communicate) and
woke a speaker if necessary. The listener stored the word immediately (to
avoid race conditions) before releasing the lock.


## Project 2: Synchronization and Priority
This project required me to solve a thread synchronization problem and
implement a priority scheduler with priority inversion.

#### Task 1: Molokai and Oahu
I used a boat lock (which I called 'mover') and three condition variables (one
for each group on each island, except for adults on Molokai because it was
unnecessary). Children went from Oahu to Molokai until there was one child
remaining on Oahu. Then, until all adults were on Molokai, a child rowed
from Oahu to Molokai, two rowed back, an adult rowed there, a child rowed back,
and this process repeated until the problem was solved.

#### Task 2: Priority Scheduling
The priority scheduler was simply a priority queue for the CPU. That's simple
enough, but the real trick is managing the priority queues to prevent deadlock
by using priority inversion (making sure that processes which hold resources
have high enough priority to run by giving them the priority of the other
threads waiting for those resources). This was managed using a notion called
"effective priority." Effective priority was calculated recursively whenever
a priority queue was modified. 


## Project 3: User Threads
In this project, I enabled some file system calls, user threads, and
multiprogramming.

#### Tasks 1 and 2: File System Calls and Multiprogramming
For this task, I used an array to hold the files that a user thread held
open. Then, I used the nachos file system stub to handle the rest of the
details.

An important multiprogramming task was to implement paging. Fortunately, nachos
provided support for calculating page numbers and such. So I just had to make
sure that I was always editing data in the write pages.

#### Task 3: User Process System Calls
When exec was called, the new process had its parent marked as the current
process and the child was added to the current process' list of children. Join
was implemented with a condition variable. Exit acquired the same lock,
woke all of the threads, and released it. Pretty simple.

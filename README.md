# didactic-waffle
CS 3053—Operating Systems

Only the files changed from the Nachos download will be added to the repo.
Other than that, the rest of the files are unchanged from the Nachos download.

Here is an overview of the projects that I did:

## Project 1: Kernel Thread System

Implementing basic thread synchronization utilities for Nachos.

#### Task 1
Implement `KThread.join()`. Note that another thread does not have to call
`join()`, but if it is called, it must be called only once. The result of
calling `join()` a second time on the same thread is undefined. A thread must
finish executing normally whether or not it is joined.

#### Task 2
Implement condition variables directly, by using interrupt enable and disable
to provide atomicity. As distributed, Nachos provides a sample implementation
that uses semaphores; your job is to provide an equivalent implementation without
directly using semaphores (you may of course still use locks, even though they
indirectly use semaphores). Once you are done, you will have two alternative
implementations that provide the exact same functionality. Your second
implementation of condition variables must reside in class
_Condition2_.

#### Task 3
Complete the implementation of the _Alarm_ class, by implementing the
`waitUntil(int x)` method. A thread calls `waitUntil` to suspend its own execution
until time has advanced to at least now `+ x`. This is useful for threads that
operate in real-time, for example, for blinking the cursor once per second.
There is no requirement that threads start running immediately after waking
up; just put them on the ready queue in the timer interrupt handler after they
have waited for at least the right amount of time. Do not fork any additional
threads to implement `waitUntil()`; you need only modify `waitUntil()` and the timer
interrupt handler. `waitUntil` is not limited to one thread; any number of threads
may call it and be suspended at any one time.

#### Task 4
Implement synchronous send and receive of one word messages (also known as
Ada-style rendezvous), using condition variables (don't use semaphores!).
Implement the _Communicator_ class with operations `void speak(int word)` and
`int listen()`.

`speak()` atomically waits until `listen()` is called on the same
_Communicator_ object, and then transfers the word over to `listen()`. Once
the transfer is made, both can return. Similarly, `listen()` waits until
`speak()` is called, at which point the transfer is made, and both can
return (`listen()` returns the word). Your solution should work even if there
are multiple speakers and listeners for the same _Communicator_ (note: this is
equivalent to a zero-length bounded buffer; since the buffer has no room, the
producer and consumer must interact directly, requiring that they wait for one
another). Each communicator should only use exactly one lock. If you're using
more than one lock, you're making things too complicated.
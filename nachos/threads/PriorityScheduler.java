package nachos.threads;

import nachos.machine.Lib;
import nachos.machine.Machine;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * A scheduler that chooses threads based on their priorities.
 * <p>
 * <p>
 * A priority scheduler associates a priority with each thread. The next thread
 * to be dequeued is always a thread with priority no less than any other
 * waiting thread's priority. Like a round-robin scheduler, the thread that is
 * dequeued is, among all the threads of the same (highest) priority, the
 * thread that has been waiting longest.
 * <p>
 * <p>
 * Essentially, a priority scheduler gives access in a round-robin fashion to
 * all the highest-priority threads, and ignores all other threads. This has
 * the potential to
 * starve a thread if there's always a thread waiting with higher priority.
 * <p>
 * <p>
 * A priority scheduler must partially solve the priority inversion problem; in
 * particular, priority must be donated through locks, and through joins.
 */
public class PriorityScheduler extends Scheduler
{
    /**
     * Allocate a new priority scheduler.
     */
    public PriorityScheduler()
    {
    }

    /**
     * Allocate a new priority thread queue.
     *
     * @param transferPriority <tt>true</tt> if this queue should
     *                         transfer priority from waiting threads
     *                         to the owning thread.
     * @return a new priority thread queue.
     */
    public ThreadQueue newThreadQueue(boolean transferPriority)
    {
        return new PriorityQueue(transferPriority);
    }

    public int getPriority(KThread thread)
    {
        Lib.assertTrue(Machine.interrupt().disabled());

        return getThreadState(thread).getPriority();
    }

    public int getEffectivePriority(KThread thread)
    {
        Lib.assertTrue(Machine.interrupt().disabled());

        return getThreadState(thread).getEffectivePriority();
    }

    public void setPriority(KThread thread, int priority)
    {
        Lib.assertTrue(Machine.interrupt().disabled());

        Lib.assertTrue(priority >= priorityMinimum &&
                priority <= priorityMaximum);

        if (getThreadState(thread).priority != priority)
            getThreadState(thread).setPriority(priority);
    }

    public boolean increasePriority()
    {
        boolean intStatus = Machine.interrupt().disable();

        KThread thread = KThread.currentThread();

        int priority = getPriority(thread);
        if (priority == priorityMaximum)
            return false;

        setPriority(thread, priority + 1);

        Machine.interrupt().restore(intStatus);
        return true;
    }

    public boolean decreasePriority()
    {
        boolean intStatus = Machine.interrupt().disable();

        KThread thread = KThread.currentThread();

        int priority = getPriority(thread);
        if (priority == priorityMinimum)
            return false;

        setPriority(thread, priority - 1);

        Machine.interrupt().restore(intStatus);
        return true;
    }

    /**
     * The default priority for a new thread. Do not change this value.
     */
    public static final int priorityDefault = 1;
    /**
     * The minimum priority that a thread can have. Do not change this value.
     */
    public static final int priorityMinimum = 0;
    /**
     * The maximum priority that a thread can have. Do not change this value.
     */
    public static final int priorityMaximum = 7;

    /**
     * Return the scheduling state of the specified thread.
     *
     * @param thread the thread whose scheduling state to return.
     * @return the scheduling state of the specified thread.
     */
    protected ThreadState getThreadState(KThread thread)
    {
        if (thread.schedulingState == null)
            thread.schedulingState = new ThreadState(thread);

        return (ThreadState) thread.schedulingState;
    }


    /**
     * A comparator which can compare all of the threads in a priority queue based on priority and then time
     */
    private class ThreadPriorityComparator<T extends ThreadState> implements Comparator<T>
    {
        /**
         * The priority queue using which to compare thread states.
         */
        private PriorityQueue associatedQueue;

        ThreadPriorityComparator(PriorityQueue forWhichQueue)
        {
            associatedQueue = forWhichQueue;
        }

        @Override public int compare(T o1, T o2)
        {
            // Maximize priority, then minimize the waiting since times
            if (o1.getEffectivePriority() != o2.getEffectivePriority())
                return -Integer.compare(o1.getEffectivePriority(), o2.getEffectivePriority());
            else
                return Long.compare(associatedQueue.waitingSince.get(o1), associatedQueue.waitingSince.get(o2));
        }
    }


    /**
     * A <tt>ThreadQueue</tt> that sorts threads by priority.
     */
    protected class PriorityQueue extends ThreadQueue
    {

        /**
         * <tt>true</tt> if this queue should transfer priority from waiting
         * threads to the owning thread.
         */
        public boolean transferPriority;

        ThreadState resourceHolder;

        /**
         * A map from the threads to how long they have been waiting in this queue
         */
        HashMap<ThreadState, Long> waitingSince = new HashMap<>();

        java.util.PriorityQueue<ThreadState> waitingForResource = new java.util.PriorityQueue<ThreadState>(
                new ThreadPriorityComparator<ThreadState>(this));


        PriorityQueue(boolean transferPriority)
        {
            this.transferPriority = transferPriority;
        }

        public void waitForAccess(KThread thread)
        {
            Lib.assertTrue(Machine.interrupt().disabled());
            getThreadState(thread).waitForAccess(this);
        }

        public void acquire(KThread thread)
        {
            Lib.assertTrue(Machine.interrupt().disabled());
            getThreadState(thread).acquire(this);
        }

        public KThread nextThread()
        {
            Lib.assertTrue(Machine.interrupt().disabled());
            if (waitingForResource.isEmpty())
                return null;

            // Give the next thread access to this resource
            acquire(waitingForResource.poll().thread);
            return resourceHolder.thread;
        }

        void addNewThread(ThreadState toAdd)
        {
            addNewThread(toAdd, true);
        }

        void removeThread(ThreadState toRemove)
        {
            removeThread(toRemove, true);
        }

        void addNewThread(ThreadState toAdd, boolean updateTime)
        {
            waitingForResource.remove(toAdd);
            if (updateTime)
                waitingSince.put(toAdd, Machine.timer().getTime());
            waitingForResource.add(toAdd);
        }

        void removeThread(ThreadState toRemove, boolean updateTime)
        {
            waitingForResource.remove(toRemove);
            if (updateTime)
                waitingSince.remove(toRemove);
        }


        /**
         * Return the next thread that <tt>nextThread()</tt> would return,
         * without modifying the state of this queue.
         *
         * @return the next thread that <tt>nextThread()</tt> would
         * return.
         */
        protected ThreadState pickNextThread()
        {
            return waitingForResource.peek();
        }

        public void print()
        {
            Lib.assertTrue(Machine.interrupt().disabled());
            // implement me (if you want)
        }
    }

    /**
     * The scheduling state of a thread. This should include the thread's
     * priority, its effective priority, any objects it owns, and the queue
     * it's waiting for, if any.
     *
     * @see KThread#schedulingState
     */
    protected class ThreadState
    {
        /**
         * The thread with which this object is associated.
         */
        protected KThread thread;

        int effectivePriority = priorityMinimum - 1;

        /**
         * The priority queues that this thread is waiting in
         */
        LinkedList<PriorityQueue> isWaitingIn = new java.util.LinkedList<>();

        /**
         * The resources that this thread holds
         */
        LinkedList<PriorityQueue> resourcesHeld = new java.util.LinkedList<>();

        /**
         * The priority of the associated thread.
         */
        protected int priority;

        /**
         * Allocate a new <tt>ThreadState</tt> object and associate it with the
         * specified thread.
         *
         * @param thread the thread this state belongs to.
         */
        public ThreadState(KThread thread)
        {
            this.thread = thread;
            effectivePriority = priorityDefault;
            setPriority(priorityDefault);
        }

        /**
         * Return the priority of the associated thread.
         *
         * @return the priority of the associated thread.
         */
        public int getPriority()
        {
            return priority;
        }

        /**
         * Return the effective priority of the associated thread.
         *
         * @return the effective priority of the associated thread.
         */
        public int getEffectivePriority()
        {
            return effectivePriority;
        }

        private void calculateEffectivePriority()
        {
            // Remove this thread from the queues that this resource is waiting on (to be added back in later)
            for (PriorityQueue possibleQueue : isWaitingIn)
                possibleQueue.removeThread(this, false);

            int adjustedPriority = priority;

            for (PriorityQueue resourceQueue : resourcesHeld)
                if (resourceQueue.transferPriority)
                    if (!resourceQueue.waitingForResource.isEmpty() &&
                            resourceQueue.waitingForResource.peek().getEffectivePriority() > adjustedPriority)
                        adjustedPriority = resourceQueue.waitingForResource.peek().getEffectivePriority();

            effectivePriority = adjustedPriority;

            // Add this back in to all necessary queues with its updated priority, and
            // update the priorities of the current resource holders; do not update the waiting time
            for (PriorityQueue possibleQueue : isWaitingIn)
                possibleQueue.addNewThread(this, false);

            for (PriorityQueue possibleQueue : isWaitingIn)
                if (possibleQueue.transferPriority && possibleQueue.resourceHolder != null)
                    possibleQueue.resourceHolder.calculateEffectivePriority(); // Recalculate effective priorities
        }

        /**
         * Set the priority of the associated thread to the specified value.
         *
         * @param priority the new priority.
         */
        public void setPriority(int priority)
        {
            this.priority = priority;
            calculateEffectivePriority();

        }

        /**
         * Called when <tt>waitForAccess(thread)</tt> (where <tt>thread</tt> is
         * the associated thread) is invoked on the specified priority queue.
         * The associated thread is therefore waiting for access to the
         * resource guarded by <tt>waitingForResource</tt>. This method is only called
         * if the associated thread cannot immediately obtain access.
         *
         * @param waitQueue the queue that the associated thread is
         *                  now waiting on.
         * @see ThreadQueue#waitForAccess
         */
        public void waitForAccess(PriorityQueue waitQueue)
        {
            // If this thread is not already waiting in the queue for this resource
            if (!isWaitingIn.contains(waitQueue))
            {
                // Remove it from the wait queue if we already hold the resource
                if (resourcesHeld.remove(waitQueue))
                {
                    waitQueue.resourceHolder = null;
                    calculateEffectivePriority(); // Now recalculate the priorities
                }

                // Add it to the wait queue for the resource, and the corresponding wait time
                waitQueue.addNewThread(this);
                isWaitingIn.add(waitQueue);

                // Now update the priority of the waiting object
                if (waitQueue.resourceHolder != null)
                    waitQueue.resourceHolder.calculateEffectivePriority();

            }
        }

        /**
         * Called when the associated thread has acquired access to whatever is
         * guarded by <tt>waitingForResource</tt>. This can occur either as a result of
         * <tt>acquire(thread)</tt> being invoked on <tt>waitingForResource</tt> (where
         * <tt>thread</tt> is the associated thread), or as a result of
         * <tt>nextThread()</tt> being invoked on <tt>waitingForResource</tt>.
         *
         * @see ThreadQueue#acquire
         * @see ThreadQueue#nextThread
         */
        public void acquire(PriorityQueue waitQueue)
        {
            // Remove the resource from the current thread
            if (waitQueue.resourceHolder != null && waitQueue.resourceHolder.resourcesHeld.remove(waitQueue))
            {
                ThreadState holder = waitQueue.resourceHolder;
                waitQueue.resourceHolder = null;
                holder.calculateEffectivePriority(); // Now recalculate the priorities with the updated lack of
                // resource hold
            }

            // Remove the associated threadstate from the corresponding priority queue, set this to be the resource
            // holding it, and add this wait queue to the list of possible queues that donate threads
            waitQueue.removeThread(this);
            waitQueue.resourceHolder = this;

            resourcesHeld.add(waitQueue);
            isWaitingIn.remove(waitQueue);

            // Recalculate the priorities now that we hold the lock
            calculateEffectivePriority();

        }
    }

}

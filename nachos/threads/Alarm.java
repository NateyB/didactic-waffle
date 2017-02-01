package nachos.threads;

import nachos.machine.Machine;

import java.util.HashMap;
import java.util.Map;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm
{

    private HashMap<KThread, Long> wakeUpMap = new HashMap<KThread, Long>();

    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     * <p>
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm()
    {
        Machine.timer().setInterruptHandler(new Runnable()
        {
            public void run()
            {
                timerInterrupt();
            }
        });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt()
    {
        KThread.yield();
        for (Map.Entry<KThread, Long> pair : wakeUpMap.entrySet())
        {
            if (pair.getValue() < Machine.timer().getTime())
                pair.getKey().ready();
        }
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     * <p>
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param x the minimum number of clock ticks to wait.
     * @see nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x)
    {
        long wakeTime = Machine.timer().getTime() + x;
        wakeUpMap.put(KThread.currentThread(), wakeTime);
        KThread.sleep();

    }
}

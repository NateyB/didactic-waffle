package nachos.threads;

import nachos.machine.Lib;

import java.util.LinkedList;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator
{

    /**
     * The lock held by both of the conditional variables
     */
    private Lock moving = new Lock();

    /**
     * The speaker conditional variable
     */
    private Condition sVariable = new Condition(moving);

    /**
     * The listener conditional variable
     */
    private Condition lVariable = new Condition(moving);

    private int numSpeakers = 0;

    private int numListeners = 0;

    /**
     * The boolean indicating if a word has been spoken
     */
    private boolean ready = false;

    /**
     * The word that was spoken, to be communicated to listener.
     *
     * As an interesting side note, this could actually be a security issue: I don't waste the computational
     * power to reset this word after passing the message; therefore, it might be able to be accessed by the
     * next listener.
     */
    private int spokenWord = 0;

    /**
     * Allocate a new communicator.
     */
    public Communicator()
    {
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     * <p>
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param word the integer to transfer.
     */
    public void speak(int word)
    {
        if (!moving.isHeldByCurrentThread())
            moving.acquire();

        while (numListeners == 0)
            KThread.sleep();

        spokenWord = word;
        ready = true;

        lVariable.wakeAll();
        numSpeakers--;

        moving.release();


    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return the integer transferred.
     */
    public int listen()
    {
        if (!moving.isHeldByCurrentThread())
            moving.acquire();

        numListeners++;

        while (!ready)
        {
            sVariable.wakeAll();
            KThread.sleep();
        }

        ready = false;
        numListeners--;

        moving.release();

        return spokenWord;

    }
}

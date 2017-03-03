package nachos.threads;

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
    private Condition2 sVariable = new Condition2(moving);

    /**
     * The listener conditional variable
     */
    private Condition2 lVariable = new Condition2(moving);

    /**
     * The number of listeners waiting
     */
    private int numListeners = 0;

    /**
     * The boolean indicating if a word has been spoken
     */
    private boolean inSpeech = false;

    /**
     * The word that was spoken, to be communicated to listener.
     * <p>
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

        while (numListeners == 0 || inSpeech)
            sVariable.sleep();

        inSpeech = true;
        spokenWord = word;

        lVariable.wake();
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

        while (!inSpeech) {
            sVariable.wake();
            lVariable.sleep();
        }


        int word = spokenWord;
        inSpeech = false;
        numListeners--;

        // Wake all of the remaining speakers to ensure that the process continues
        sVariable.wakeAll();
        moving.release();

        return word;

    }
}

package nachos.threads;

import nachos.ag.BoatGrader;
import nachos.machine.Lib;

public class Boat
{
    /**
     * This lock determines which agent is acting
     */
    private static Lock mover = new Lock();
    /**
     * This is the condition that the adults wait on, sleeping until there is one child with the boat on the island
     */
    private static Condition2 oahuAdults = new Condition2(mover);

    /**
     * This is the condition that the children wait on, sleeping until two children or an adult arrives with the boat
     */
    private static Condition2 molokaiChildren = new Condition2(mover);

    /**
     * This is the condition that the children wait on, sleeping until two children or an adult arrives with the boat
     */
    private static Condition2 oahuChildren = new Condition2(mover);

    /**
     * A lock that indicates when the main thread should terminate
     */
    private static Lock termination = new Lock();

    /**
     * The condition variable that is woken when termination is permitted
     */
    private static Condition2 canTerminate = new Condition2(termination);

    /**
     * This semaphore ensures that all of the children are initialized before the main thread terminates
     */
    private static Semaphore initialized = new Semaphore(0);


    private static int totalPeople = 0;

    /**
     * Number of children on Oahu
     */
    private static int numOahuChildren = 0;

    /**
     * Number of adults on Oahu
     */
    private static int numOahuAdults = 0;

    /**
     * Number of people on Molokai
     */
    private static int numMolokai = 0;

    /**
     * True if the boat is at Oahu
     */
    private static boolean boatAtOahu = true;

    /**
     * Did a child just travel on their own, and the next child should ride?
     */
    private static boolean shouldRide = false;

    static BoatGrader bg;

    public static void selfTest()
    {
        BoatGrader b = new BoatGrader();

//       System.out.println("\n ***Testing Boats with only 2 children***");
//       begin(0, 2, b);

//        System.out.println("\n ***Testing Boats with 2 children, 3 adults***");
//        begin(3, 2, b);

        System.out.println("\n ***Testing Boats with 3 children, 4 adult***");
        begin(4, 3, b);

//        System.out.println("\n ***Testing Boats with 10 children, 10 adults***");
//        begin(10, 10, b);

    }

    public static void begin(int adults, int children, BoatGrader b)
    {
        // Store the externally generated autograder in a class
        // variable to be accessible by children.
        bg = b;

        // Instantiate global variables here

        // Create threads here. See section 3.4 of the Nachos for Java
        // Walkthrough linked from the projects page.

        totalPeople = adults + children;

        termination.acquire();

        for (int i = 0; i < adults; i++)
        {
            Runnable r = new Runnable()
            {
                public void run()
                {
                    AdultItinerary();
                    System.out.println("Adult terminating.");
                }
            };
            KThread t = new KThread(r);
            t.setName("Adult Thread " + i);
            t.fork();
        }

        for (int i = 0; i < adults; i++)
            initialized.P();


        for (int i = 0; i < children; i++)
        {
            Runnable r = new Runnable()
            {
                public void run()
                {
                    ChildItinerary();
                    System.out.println("Child terminating.");
                }
            };
            KThread t = new KThread(r);
            t.setName("Child Thread " + i);
            t.fork();
        }

        for (int i = 0; i < children; i++)
            initialized.P();


        System.out.println("Threads initialized.");
        mover.acquire();

        System.out.println("Starting.");
        oahuChildren.wake();
        mover.release();

        canTerminate.sleep();
        termination.release();
        System.out.println("Terminating (note that this does not imply that the test was passed).");
    }

    static void AdultItinerary()
    {
        /* This is where you should put your solutions. Make calls
           to the BoatGrader to show that it is synchronized. For
           example:
               bg.AdultRowToMolokai();
           indicates that an adult has rowed the boat across to Molokai
	    */

        mover.acquire();
        numOahuAdults++;
        initialized.V();
        oahuAdults.sleep();
        numOahuAdults--;
        bg.AdultRowToMolokai();
        boatAtOahu = false;
        numMolokai++;
        molokaiChildren.wake();
        mover.release();
    }

    private static void completionCheck()
    {
        if (numMolokai == totalPeople)
        {
            termination.acquire();
            canTerminate.wake();
            termination.release();
        }
    }

    /**
     * Move the child to Molokai, by riding or rowing if applicable
     * Caller must hold the mover lock already
     */
    private static void childGoToMolokai()
    {
        Lib.assertTrue(mover.isHeldByCurrentThread(), "Must hold the mover lock");
        numMolokai++;
        numOahuChildren--;

        if (shouldRide)
        {
            bg.ChildRideToMolokai();
            shouldRide = false;
            completionCheck();
        } else
        {
            bg.ChildRowToMolokai();
            shouldRide = true;
            completionCheck();
            boatAtOahu = false;

            oahuChildren.wake();
            molokaiChildren.sleep();
        }

        childGoToOahu();
    }

    /**
     * Move the child to Molokai, by riding or rowing if applicable
     * Caller must hold the mover lock already
     */
    private static void childGoToOahu()
    {
        Lib.assertTrue(mover.isHeldByCurrentThread(), "Must hold the mover lock when going to Oahu");
        numMolokai--;
        numOahuChildren++;

        bg.ChildRowToOahu();
        boatAtOahu = true;
    }

    /**
     * The child is guaranteed to be at Oahu holding the movement lock when this thread starts
     */
    private static void childReady()
    {
        while (true)
        {
            if (numOahuChildren > 1)
                childGoToMolokai();
            else if (boatAtOahu && numOahuAdults > 0)
            {
                oahuAdults.wake();
                oahuChildren.sleep();
            } else if (boatAtOahu || shouldRide)
            {
                childGoToMolokai();
            }
        }
    }

    static void ChildItinerary()
    {
        // Increment the number of children (so that we can get an accurate count of the number of people)
        mover.acquire();
        numOahuChildren++;
        initialized.V();

        oahuChildren.sleep();

        childReady();
    }

    static void SampleItinerary()
    {
        // Please note that this isn't a valid solution (you can't fit
        // all of them on the boat). Please also note that you may not
        // have a single thread calculate a solution and then just play
        // it back at the autograder -- you will be caught.
        System.out.println("\n ***Everyone piles on the boat and goes to Molokai***");
        bg.AdultRowToMolokai();
        bg.ChildRideToMolokai();
        bg.AdultRideToMolokai();
        bg.ChildRideToMolokai();
    }

}


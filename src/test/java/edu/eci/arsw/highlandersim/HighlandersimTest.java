package edu.eci.arsw.highlandersim;

import static org.junit.Assert.*;

import edu.eci.arsw.highlandersim.ControlFrame;
import org.junit.Test;
import org.junit.Before;
import junit.framework.TestCase;



public class HighlandersimTest  {
    private static Double TOLERANCE=0.1d;
    public ControlFrame a;
    @Before
    public void setUp(){
        a = new ControlFrame();
    }


    /**
     * Rigourous Test :-)
     */
    @Test
    public void invariantTest(){
        a.start();
        for(int i=0; i<50; i++){

            a.pause();
            ControlFrame.count++;
            try {
                Thread.sleep(1100);
            } catch (InterruptedException e) {
            }
            assertEquals(a.getTotalLife(), 300);
            a.resume();

        }
    }


}
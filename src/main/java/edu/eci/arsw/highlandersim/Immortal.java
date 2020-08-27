package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private AtomicInteger health = new AtomicInteger(0);
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private boolean alive = true;

    public static Object pauseLock = new Object();
    //El número de pausados será protegido con el lock de pausa
    public static AtomicInteger pausedThreads = new AtomicInteger(0);

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health.set(health);
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {

        while (!ControlFrame.stopped.get() && alive) {
            if(ControlFrame.paused.get()) {
                pause();
            } else {
                Immortal im = null;
                int myIndex = immortalsPopulation.indexOf(this);
                int nextFighterIndex;
                boolean done = false;
                //avoid self-fight
                while(!done) {
                    nextFighterIndex = r.nextInt(immortalsPopulation.size());
                    if (nextFighterIndex == myIndex) {
                        nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
                    }
                    try {
                        im = immortalsPopulation.get(nextFighterIndex);
                        done = true;
                    } catch(IndexOutOfBoundsException e){
                    }
                }
                this.fight(im);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void fight(Immortal i2) {
        if(getHealth() > 0) {
            boolean done = false;
            synchronized (i2) {
                if (i2.getHealth() > 0) {
                    i2.changeHealth(-defaultDamageValue);
                    done = true;
                }
            }
            synchronized (this) {
                if (done) {
                    changeHealth(defaultDamageValue);
                    updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
                } else {
                    updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                }
            }
        } else {
            alive = false;
            immortalsPopulation.remove(this);
        }
    }

    public void changeHealth(int v) {
        health.addAndGet(v);
    }

    public int getHealth() {
        return health.get();
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }


    public synchronized void pause(){
        synchronized (ControlFrame.pauseLock){
            if(immortalsPopulation.size() <= pausedThreads.incrementAndGet()){
                ControlFrame.pauseLock.notifyAll();
            }
        }
        synchronized (pauseLock){
            if(ControlFrame.paused.get()){
                try {
                    System.out.println("DURMIENDO "+ ControlFrame.count);
                    pauseLock.wait();
                    System.out.println("DESPIERTO " + ControlFrame.count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        this.notifyAll();
    }

    public Object getPauseLock(){
        return pauseLock;
    }
}

package org.example;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CrptApi {
    private final TimeUnit timeUnit;
    private final int requestLimit;
    private final AtomicInteger requestCounter;
    private final Object lock = new Object();

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        this.requestCounter = new AtomicInteger(0);
    }

    public void createDocument(String documentJson, String signature) {
        synchronized (lock) {
            if (requestCounter.get() >= requestLimit) {
                try {
                    lock.wait(timeUnit.toMillis(1));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            System.out.println("Making POST request with document: " + documentJson + " and signature: " + signature);

            requestCounter.incrementAndGet();
        }
    }

    public static void main(String[] args) {
        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 5);
        for (int i = 0; i < 10; i++) {
            crptApi.createDocument("sampleDocumentJson", "sampleSignature");
        }
    }
}
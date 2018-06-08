package test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class ServiceCHelper {

    @Inject
    @ConfigProperty(name="serviceC.url")
    private String serviceCURL;

    @Inject
    @RestClient
    private ServiceC client;

    @Inject
    private ServiceCHelper self;

    public String getProperty(String name) {
        return client.getProp("none", name).getValue();
    }

    public Prop getPropertyEasy(String name) {
        return new Prop(name, getProperty(name));
    }

    @Retry()
    public Prop getPropertyWithRetry(String name)  {
        return new Prop(name, getProperty(name));
    }

    @Timeout(500)
    public Prop getPropertyWithTimeout(String name)  {
        try {
            return self.getPropertyAsync(name).get();
        } catch (Exception e) {
            throw unwrapException(e);
        }
    }

    @Timeout(500)
    @Fallback(fallbackMethod="fallback")
    public Prop getPropertyWithTimeoutAndFallback(String name) {
        try {
            return self.getPropertyAsync(name).get();
        } catch (Exception e) {
            throw unwrapException(e);
        }
    }

    public Prop fallback(String name) {
        return new Prop(name, System.getProperty(name));
    }

    @Asynchronous
    public Future<Prop> getPropertyAsync(String name) {
        return CompletableFuture.completedFuture(new Prop(name, getProperty(name)));
    }

    private RuntimeException unwrapException(Throwable e) {
        if (e instanceof ExecutionException) {
            e = e.getCause();
        }

        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }

        return new RuntimeException(e);
    }
}
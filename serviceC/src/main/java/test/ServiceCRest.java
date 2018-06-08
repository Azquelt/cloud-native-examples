package test;

import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Timed;

@Path("props")
@ApplicationScoped
public class ServiceCRest {
    @Inject
    private MyBean bean;

    private Random random = new Random();

    @Gauge(name="numProps", displayName="Number of properties", unit="properties", description="The total number of properties")
    private int numProps() {
        return bean.getProperties().size();
    }

    @Timed(name="getPropertyTime", displayName="Call duration", description="Time spend in call")
    @Counted(name="getPropertyCount", absolute=false, monotonic=true, displayName="Times called", description="Number of times called")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("{propName}")
    public Response getProperty(@PathParam("propName") String propName) {
        Prop p = new Prop(propName, bean.getProperty(propName));
        ResponseBuilder builder = null;

        String mode = "good";

        if ("slow".equals(mode)) {
            try {
                Thread.sleep(5000);
            } catch (Exception e) {}
        } else if ("fail".equals(mode)) {
            builder = Response.status(500);
        } else if ("sometimes".equals(mode)) {
            if (random.nextInt(10) < 3) {
                builder = Response.status(500);
            }
        }

        if (builder == null) {
            builder = Response.ok(p);
        }

        return builder.build();
    }
}

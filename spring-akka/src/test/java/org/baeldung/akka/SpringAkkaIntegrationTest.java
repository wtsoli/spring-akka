package org.baeldung.akka;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.util.Timeout;
import org.baeldung.akka.GreetingActor.Greet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import static akka.pattern.Patterns.ask;
import static org.baeldung.akka.SpringExtension.SPRING_EXTENSION_PROVIDER;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfiguration.class)
public class SpringAkkaIntegrationTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private ActorSystem system;

    @Test
    public void whenCallingGreetingActor_thenActorGreetsTheCaller() throws Exception {
        ActorRef greeter = system.actorOf(SPRING_EXTENSION_PROVIDER.get(system).props("greetingActor"), "greeter");

        FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);

        Future<Object> result = ask(greeter, new Greet("John"), timeout);

		Object msg = Await.result(result, duration);

		System.out.println("msg received: " + msg);

        Assert.assertEquals("correct msg should be received!", "Hello, John", msg);
    }

    @After
    public void tearDown() {
        system.shutdown();
        system.awaitTermination();
    }

}

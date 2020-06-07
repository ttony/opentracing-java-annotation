package io.opentracing.contrib.annotation;

import io.opentracing.contrib.annotation.sample.ClassWithCurrentSpanAnnotation;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import io.opentracing.util.GlobalTracer;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.Message;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = {TestConfig.class, ClassWithCurrentSpanAnnotation.class}
)
public class CurrentSpanLibraryTest {

    @Mock
    private Message message;

    private static MockTracer tracer;

    @Autowired
    private ClassWithCurrentSpanAnnotation classWithCurrentSpanAnnotation;

    @BeforeClass
    public static void init() {
        tracer = new MockTracer();
        GlobalTracer.registerIfAbsent(tracer);
    }

    @After
    public void tearDown() {
        tracer.reset();
    }

    @Test
    public void testWithNoArgs() {
        classWithCurrentSpanAnnotation.withEmptyArgs();
    }

    @Test
    public void testWithSpanArgs() {
        classWithCurrentSpanAnnotation.withSpanArgs(null);
    }

    @Test(expected = NullPointerException.class)
    public void testWithNPE() {

        // When
        classWithCurrentSpanAnnotation.withNPEThrown();

        // Then
        List<MockSpan> mockSpans = tracer.finishedSpans();
        assertThat(mockSpans.size(), is(1));

        MockSpan mockSpan = mockSpans.get(0);
        assertThat(mockSpan.logEntries().size(), is(1));
        assertThat(mockSpan.logEntries().get(0).fields().get("event"), is("error"));
        assertThat(mockSpan.logEntries().get(0).fields().get("error.object").getClass(), is(NullPointerException.class));
        assertThat(mockSpan.logEntries().get(0).fields().get("message").getClass(), is(""));
    }


    @Test
    public void testWithSpanArgsOnExtraLogic() {

        // When
        classWithCurrentSpanAnnotation.withExtraLogicOnSpanArgs(null);

        // Then
        List<MockSpan> mockSpans = tracer.finishedSpans();
        assertThat(mockSpans.size(), is(1));

        MockSpan mockSpan = mockSpans.get(0);
        assertThat(mockSpan.operationName(), is("ClassWithNewSpanAnnotation.withExtraLogicOnSpanArgs"));
        assertThat(mockSpan.logEntries().size(), is(1));
        assertThat(mockSpan.logEntries().get(0).fields().get("event"), is("this is event log"));

        assertThat(mockSpan.tags().isEmpty(), is(true));
    }

    @Test
    public void testNewSpanCreation() {

        // When
        classWithCurrentSpanAnnotation.withEmptyArgs();

        // Then
        List<MockSpan> mockSpans = tracer.finishedSpans();
        assertThat(mockSpans.size(), is(1));

        MockSpan mockSpan = mockSpans.get(0);
        assertThat(mockSpan.operationName(), is("ClassWithNewSpanAnnotation.withEmptyArgs"));

        assertThat(mockSpan.tags().isEmpty(), is(true));
    }

    @Test
    public void testNewSpanCreationWithTag() {

        // When
        classWithCurrentSpanAnnotation.withTag("tag-value");

        // Then
        List<MockSpan> mockSpans = tracer.finishedSpans();
        assertThat(mockSpans.size(), is(1));

        MockSpan mockSpan = mockSpans.get(0);
        assertThat(mockSpan.operationName(), is("ClassWithNewSpanAnnotation.withTag"));

        assertThat(mockSpan.tags().isEmpty(), is(false));
        assertThat(mockSpan.tags().get("tag-name"), is("tag-value"));
    }

    @Test
    public void testNewSpanCreationWithAdvanceTag() throws Exception {
        // Given
        when(message.getJMSMessageID()).thenReturn("msg-id-sample");

        // When
        classWithCurrentSpanAnnotation.withAdvanceTag(message);

        // Then
        List<MockSpan> mockSpans = tracer.finishedSpans();
        assertThat(mockSpans.size(), is(1));

        MockSpan mockSpan = mockSpans.get(0);
        assertThat(mockSpan.operationName(), is("ClassWithNewSpanAnnotation.withAdvanceTag"));

        assertThat(mockSpan.tags().isEmpty(), is(false));
        assertThat(mockSpan.tags().get("msg-id"), is("msg-id-sample"));
    }

    @Test
    public void testNewSpanCreationWithAdvanceTagArgumentNotMatch() throws Exception {
        // Given
        when(message.getJMSMessageID()).thenReturn("msg-id-sample");

        // When
        classWithCurrentSpanAnnotation.withAdvanceTagNotMatchedArgument("tony");

        // Then
        List<MockSpan> mockSpans = tracer.finishedSpans();
        assertThat(mockSpans.size(), is(1));

        MockSpan mockSpan = mockSpans.get(0);
        assertThat(mockSpan.operationName(), is("ClassWithNewSpanAnnotation.withAdvanceTagNotMatchedArgument"));

        assertThat(mockSpan.tags().isEmpty(), is(true));
    }

    @Test
    public void testNewSpanCreationWithUnsupportedValueTag() {

        // When
        classWithCurrentSpanAnnotation.withUnsupportedValueTag("tony".getBytes());

        // Then
        List<MockSpan> mockSpans = tracer.finishedSpans();
        assertThat(mockSpans.size(), is(1));

        MockSpan mockSpan = mockSpans.get(0);
        assertThat(mockSpan.operationName(), is("ClassWithNewSpanAnnotation.withUnsupportedValueTag"));

        assertThat(mockSpan.tags().isEmpty(), is(true));
    }
}

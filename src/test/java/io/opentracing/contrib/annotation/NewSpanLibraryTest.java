package io.opentracing.contrib.annotation;

import io.opentracing.contrib.annotation.sample.ClassWithNewSpanAnnotation;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import io.opentracing.util.GlobalTracer;
import org.junit.After;
import org.junit.BeforeClass;
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

@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = {TestConfig.class, ClassWithNewSpanAnnotation.class}
)
public class NewSpanLibraryTest {

    @Mock
    private Message message;

    private static MockTracer tracer;

    @Autowired
    private ClassWithNewSpanAnnotation classWithNewSpanAnnotation;

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
        classWithNewSpanAnnotation.withEmptyArgs();
    }

    @Test
    public void testWithSpanArgs() {
        classWithNewSpanAnnotation.withSpanArgs(null);
    }

    @Test(expected = NullPointerException.class)
    public void testWithNPE() {

        // When
        classWithNewSpanAnnotation.withNPEThrown();

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
        classWithNewSpanAnnotation.withExtraLogicOnSpanArgs(null);

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
        classWithNewSpanAnnotation.withEmptyArgs();

        // Then
        List<MockSpan> mockSpans = tracer.finishedSpans();
        assertThat(mockSpans.size(), is(1));

        MockSpan mockSpan = mockSpans.get(0);
        assertThat(mockSpan.operationName(), is("ClassWithNewSpanAnnotation.withEmptyArgs"));

        assertThat(mockSpan.tags().isEmpty(), is(true));
    }

    @Test
    public void testNewSpanCreationWithOverrideOperationName() {

        // When
        classWithNewSpanAnnotation.withOperationName();

        // Then
        List<MockSpan> mockSpans = tracer.finishedSpans();
        assertThat(mockSpans.size(), is(1));

        MockSpan mockSpan = mockSpans.get(0);
        assertThat(mockSpan.operationName(), is("newName"));

        assertThat(mockSpan.tags().isEmpty(), is(true));
    }

    @Test
    public void testNewSpanCreationWithTag() {

        // When
        classWithNewSpanAnnotation.withTag("tag-value");

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
        classWithNewSpanAnnotation.withAdvanceTag(message);

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
        classWithNewSpanAnnotation.withAdvanceTagNotMatchedArgument("tony");

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
        classWithNewSpanAnnotation.withUnsupportedValueTag("tony".getBytes());

        // Then
        List<MockSpan> mockSpans = tracer.finishedSpans();
        assertThat(mockSpans.size(), is(1));

        MockSpan mockSpan = mockSpans.get(0);
        assertThat(mockSpan.operationName(), is("ClassWithNewSpanAnnotation.withUnsupportedValueTag"));

        assertThat(mockSpan.tags().isEmpty(), is(true));
    }
}

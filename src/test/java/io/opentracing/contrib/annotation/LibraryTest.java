package io.opentracing.contrib.annotation;

import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import io.opentracing.util.GlobalTracer;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.jms.Message;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LibraryTest {

    @Mock private Message message;

    private static MockTracer tracer;

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
        new ClassWithNewSpanAnnotation().withEmptyArgs();
    }

    @Test
    public void testWithSpanArgs() {
        new ClassWithNewSpanAnnotation().withSpanArgs(null);
    }

    @Test(expected = NullPointerException.class)
    public void testWithNPE() {

        // When
        new ClassWithNewSpanAnnotation().withNPEThrown();

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
        new ClassWithNewSpanAnnotation().withExtraLogicOnSpanArgs(null);

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
        new ClassWithNewSpanAnnotation().withEmptyArgs();

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
        new ClassWithNewSpanAnnotation().withOperationName();

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
        new ClassWithNewSpanAnnotation().withTag("tag-value");

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
        new ClassWithNewSpanAnnotation().withAdvanceTag(message);

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
        new ClassWithNewSpanAnnotation().withAdvanceTagNotMatchedArgument("tony");

        // Then
        List<MockSpan> mockSpans = tracer.finishedSpans();
        assertThat(mockSpans.size(), is(1));

        MockSpan mockSpan = mockSpans.get(0);
        assertThat(mockSpan.operationName(), is("ClassWithNewSpanAnnotation.withAdvanceTagNotMatchedArgument"));

        assertThat(mockSpan.tags().isEmpty(), is(true));
    }

    @Test
    public void testNewSpanCreationWithInternalMethodCalled() {

        // When
        new ClassWithNewSpanAnnotation().internalMethodCallwithTag();

        // Then
        List<MockSpan> mockSpans = tracer.finishedSpans();
        assertThat(mockSpans.size(), is(1));

        MockSpan mockSpan = mockSpans.get(0);
        assertThat(mockSpan.operationName(), is("ClassWithNewSpanAnnotation.internalWithTag"));

        assertThat(mockSpan.tags().isEmpty(), is(false));
        assertThat(mockSpan.tags().get("tag-name"), is("another-tag-value"));
    }

    @Test
    public void testNewSpanCreationWithUnsupportedValueTag() {

        // When
        new ClassWithNewSpanAnnotation().withUnsupportedValueTag("tony".getBytes());

        // Then
        List<MockSpan> mockSpans = tracer.finishedSpans();
        assertThat(mockSpans.size(), is(1));

        MockSpan mockSpan = mockSpans.get(0);
        assertThat(mockSpan.operationName(), is("ClassWithNewSpanAnnotation.withUnsupportedValueTag"));

        assertThat(mockSpan.tags().isEmpty(), is(true));
    }
}

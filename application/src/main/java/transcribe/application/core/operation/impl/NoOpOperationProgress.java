package transcribe.application.core.operation.impl;

import transcribe.application.core.operation.OperationProgress;
import transcribe.core.core.no_op.NoOp;

public class NoOpOperationProgress implements OperationProgress {

    @Override
    public void open() {
        NoOp.runnable().run();
    }

    @Override
    public void close() {
        NoOp.runnable().run();
    }

}

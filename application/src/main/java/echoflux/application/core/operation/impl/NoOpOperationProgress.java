package echoflux.application.core.operation.impl;

import echoflux.application.core.operation.OperationProgress;
import echoflux.core.core.no_op.NoOp;

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

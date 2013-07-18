package mobi.hubtech.goacg.request;

public class MockRequestTask<T extends BaseResponse> extends RequestTask<T> {

    public MockRequestTask(Class<T> classOfT) {
        super(classOfT);
    }

    @Override
    protected T doInBackground(IRequest... params) {
        return null;
    }
}

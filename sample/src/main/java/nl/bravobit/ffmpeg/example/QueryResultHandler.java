package nl.bravobit.ffmpeg.example;

public interface QueryResultHandler {

    /**
     * Handle the result of a query.
     * 
     * @param result
     *            The result to handle
     */
    public void handleQueryResult(QueryResult result);

    public void handleEmptyResult(QueryResult result);
}

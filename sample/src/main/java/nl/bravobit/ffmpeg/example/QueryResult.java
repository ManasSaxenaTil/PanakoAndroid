package nl.bravobit.ffmpeg.example;

public class QueryResult {

    public final String identifier;
    public final String description;
    public final double score;
    public final double time;
    public final double queryTimeOffsetStart;
    public final double queryTimeOffsetStop;
    public final double timeFactor;
    public final double frequencyFactor;

    /**
     * @param queryTimeOffsetStart
     *            The start time offset in the query. The match is found at
     *            <code>queryTimeOffsetStart+time</code>.
     * @param queryTimeOffsetStop
     *            The stop time offset of the query.
     * @param identifier
     *            The internal identifier of the matched audio
     * @param description
     *            The meta-data, description of the matched audio
     * @param score
     *            The score for the match
     * @param time
     *            The starting position in the matched audio, in seconds.
     * @param timeFactor
     *            The factor (percentage) of change in time. 110 means 10% speedup
     *            compared to the reference. 90 means 10% slower than reference.
     * @param frequencyFactor
     *            The factor (percentage) of change in frequency. 110 means 10%
     *            higher frequency compared to the reference. 90 means a 10% lower
     *            frequency.
     */
    public QueryResult(double queryTimeOffsetStart, double queryTimeOffsetStop, String identifier, String description,
            double score, double time, double timeFactor, double frequencyFactor) {
        this.queryTimeOffsetStart = queryTimeOffsetStart;
        this.queryTimeOffsetStop = queryTimeOffsetStop;
        this.identifier = identifier;
        this.description = description;
        this.score = score;
        this.time = time;
        this.timeFactor = timeFactor;
        this.frequencyFactor = frequencyFactor;
    }

    public static QueryResult emptyQueryResult(double queryTimeOffsetStart, double queryTimeOffsetStop) {
        return new QueryResult(queryTimeOffsetStart, queryTimeOffsetStop, null, null, -1, -1, -1, -1);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("QueryResult [identifier=");
        builder.append(identifier);
        builder.append(", description=");
        builder.append(description);
        builder.append(", score=");
        builder.append(score);
        builder.append(", time=");
        builder.append(time);
        builder.append(", queryTimeOffsetStart=");
        builder.append(queryTimeOffsetStart);
        builder.append(", queryTimeOffsetStop=");
        builder.append(queryTimeOffsetStop);
        builder.append(", timeFactor=");
        builder.append(timeFactor);
        builder.append(", frequencyFactor=");
        builder.append(frequencyFactor);
        builder.append("]");
        return builder.toString();
    }
}

package engineer.hyper.agentic.records;

/**
 * Record representing the response from the routing classification process.
 *
 * <p>
 * This record is used by the {@link RoutingWorkflow} to
 * capture and communicate routing decisions made by the LLM classifier.
 *
 * @param reasoning A detailed explanation of why a particular route was chosen,
 *                  considering factors like key terms, user intent, and urgency
 *                  level
 * @param selection The name of the selected route that will handle the input
 * @author Christian Tzolov
 * @see RoutingWorkflow
 */
public record RoutingResponse(
        /**
         * The reasoning behind the route selection, explaining why this particular
         * route was chosen based on the input analysis.
         */
        String reasoning,

        /**
         * The selected route name that will handle the input based on the
         * classification analysis.
         */
        String selection) {
}
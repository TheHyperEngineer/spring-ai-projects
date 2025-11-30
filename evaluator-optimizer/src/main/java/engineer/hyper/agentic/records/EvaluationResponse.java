package engineer.hyper.agentic.records;

/**
	 * Represents an evaluation response. Contains the evaluation result and
	 * detailed feedback.
	 * 
	 * @param evaluation The evaluation result (PASS, NEEDS_IMPROVEMENT, or FAIL)
	 * @param feedback   Detailed feedback for improvement
	 */
	public record EvaluationResponse(Evaluation evaluation, String feedback) {

		public enum Evaluation {
			PASS, NEEDS_IMPROVEMENT, FAIL
		}
	}
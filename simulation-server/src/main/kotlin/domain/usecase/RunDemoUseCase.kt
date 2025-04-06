package domain.usecase

/**
 * Use case for running the buffer monitoring demo.
 */
interface RunDemoUseCase {
    /**
     * Runs the buffer monitoring demo with the specified configuration.
     *
     * @param port The port to run the visualization server on
     */
    suspend operator fun invoke(
        port: Int = 8080
    )
}

import di.AppModule
import kotlinx.coroutines.runBlocking

/**
 * Main entry point for the application.
 * Uses Clean Architecture to organize code into layers:
 * - Domain: Core business logic and entities
 * - Data: Implementation of repositories and data sources
 * - Presentation: UI components and visualization
 * - DI: Dependency injection to wire everything together
 */
fun main() = runBlocking {
    // http://localhost:8080/index.html
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    val runDemoUseCase = AppModule.createRunDemoUseCase(port)
    runDemoUseCase.invoke(port)
}

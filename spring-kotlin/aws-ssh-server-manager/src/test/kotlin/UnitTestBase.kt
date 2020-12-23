import dev.santos.awssshservermanager.application.service.RemoveExpiredPermissionsJob
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
abstract class UnitTestBase {
  // Not worth testing that the background jobs lib does its job
  @MockBean
  lateinit var removeExpiredPermissionsJob: RemoveExpiredPermissionsJob
}

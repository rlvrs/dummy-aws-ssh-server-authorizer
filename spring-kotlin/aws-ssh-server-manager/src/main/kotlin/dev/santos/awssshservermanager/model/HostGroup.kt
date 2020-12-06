package dev.santos.awssshservermanager.model

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

class HostGroupMatcher(
  val tagName: String,
  val tagValues: List<String>
)

@Entity(name = "host_group")
@Table
@TypeDef(
  name = "jsonb",
  typeClass = JsonBinaryType::class
)
class HostGroup(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long = 0,
  var tenantId: Long = 0,
  var name: String = "",
  @Type(type = "jsonb")
  @Column(columnDefinition = "jsonb")
  var matchers: List<HostGroupMatcher>,
  var policyArn: String?,
  var policyVersionId: String?
)

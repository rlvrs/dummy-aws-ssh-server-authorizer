package dev.santos.awssshservermanager.model

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import javax.persistence.*

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
        val id:Long=0,
        val tenantId:Long=0,
        val name: String="",
        @Type(type = "jsonb")
        @Column(columnDefinition = "jsonb")
        val matchers: List<HostGroupMatcher>,
        val policyArn: String,
        val policyVersionId: String
)
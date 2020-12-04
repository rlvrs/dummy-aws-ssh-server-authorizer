package dev.santos.awssshservermanager.mapper

import dev.santos.awssshservermanager.dto.CreateHostGroupDto
import dev.santos.awssshservermanager.lib.aws.model.IamPolicy
import dev.santos.awssshservermanager.model.HostGroup
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.ReportingPolicy

@Mapper(
  componentModel = "spring",
  injectionStrategy = InjectionStrategy.CONSTRUCTOR,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface HostGroupMapper {
  @Mappings(
    Mapping(source = "createHostGroupDto.tenantId", target = "tenantId"),
    Mapping(source = "createHostGroupDto.name", target = "name"),
    Mapping(source = "createHostGroupDto.matchers", target = "matchers"),
    Mapping(source = "iamPolicy.arn", target = "policyArn"),
    Mapping(source = "iamPolicy.versionId", target = "policyVersionId")
  )
  fun toHostGroup(createHostGroupDto: CreateHostGroupDto, iamPolicy: IamPolicy): HostGroup
}

package es.in2.desmos.infrastructure.configs.cache;

import es.in2.desmos.domain.models.AccessNodeYamlData;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class AccessNodeMemoryStore {
    private AccessNodeYamlData organizations;
}
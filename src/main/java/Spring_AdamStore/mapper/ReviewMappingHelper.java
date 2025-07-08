package Spring_AdamStore.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewMappingHelper {

    private final ObjectMapper objectMapper;

    public JsonNode toJsonNode(List<String> imageUrls) {
        return objectMapper.valueToTree(imageUrls);
    }

}

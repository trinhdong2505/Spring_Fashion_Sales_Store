package Spring_AdamStore.service;

import Spring_AdamStore.dto.response.DistrictResponse;
import Spring_AdamStore.dto.response.PageResponse;
import Spring_AdamStore.dto.response.WardResponse;
import Spring_AdamStore.entity.District;
import Spring_AdamStore.entity.Ward;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WardService {

    WardResponse fetchById(String id);

    PageResponse<WardResponse> fetchAll(Pageable pageable);

    List<Ward> loadWardsFromGhn(int districtId);
}

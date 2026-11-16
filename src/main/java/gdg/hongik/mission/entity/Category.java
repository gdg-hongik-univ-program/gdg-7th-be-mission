package gdg.hongik.mission.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    CLOTHES("의류"),
    ELECTRONICS("전자기기"),
    COSMETICS("화장품"),
    FOOD("식품");

    @JsonValue // 응답 시 CLOTHES → "의류", 요청 시 "의류" → CLOTHES 로 양방향 변환됩니다.
    private final String label;
}
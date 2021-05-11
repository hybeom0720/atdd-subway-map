package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Deque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.exception.section.InvalidDistanceException;
import wooteco.subway.exception.station.NotFoundStationException;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.dto.DeleteStationDto;
import wooteco.subway.section.dto.SectionServiceDto;
import wooteco.subway.station.Station;
import wooteco.subway.station.dao.StationDao;

@Sql("classpath:initializeData.sql")
@SpringBootTest
public class SectionServiceTest {

    private Long lineId;
    private Long stationSinSeolId;
    private Long stationDongMyoId;
    private Long stationDongDaeMoonId;
    private int distance;

    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private LineDao lineDao;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private SectionService sectionService;

    @BeforeEach
    void setUp() {
        lineId = 1L;
        stationSinSeolId = 1L;
        stationDongMyoId = 2L;
        stationDongDaeMoonId = 3L;
        distance = 10;

        SectionServiceDto sinSeolAndDongMyoDto = new SectionServiceDto(lineId, stationSinSeolId,
            stationDongMyoId, distance);
        SectionServiceDto dongMyoAndDongDaeMoonDto = new SectionServiceDto(lineId, stationDongMyoId,
            stationDongDaeMoonId, distance);
        sectionService.saveByLineCreate(sinSeolAndDongMyoDto);
        sectionService.save(dongMyoAndDongDaeMoonDto);
    }

    @Test
    @DisplayName("Section 추가")
    void createSectionWithLineAndStations() {
        // given
        long sectionId = 3L;
        long downStationId = 4L;
        long upStationId = 3L;
        int distance = 10;
        String name = "회기역";
        stationDao.save(new Station(name));

        // when
        SectionServiceDto dongDaeMoonAndHaegiDto = new SectionServiceDto(lineId, upStationId,
            downStationId, distance);
        SectionServiceDto savedDongDaeMoonAndHaegiDto = sectionService.save(dongDaeMoonAndHaegiDto);

        // then
        assertThat(savedDongDaeMoonAndHaegiDto.getId()).isEqualTo(sectionId);
        assertThat(savedDongDaeMoonAndHaegiDto.getLineId()).isEqualTo(lineId);
        assertThat(savedDongDaeMoonAndHaegiDto.getUpStationId()).isEqualTo(upStationId);
        assertThat(savedDongDaeMoonAndHaegiDto.getDownStationId()).isEqualTo(downStationId);
        assertThat(savedDongDaeMoonAndHaegiDto.getDistance()).isEqualTo(distance);
    }

    @Test
    @DisplayName("역 사이에 추가")
    void createSectionBetweenSections() {
        // given
        long targetUpStationId = 2L;
        long targetDownStationId = 9L;
        int distance = 5;
        String name = "회기역";
        stationDao.save(new Station(name));

        // when
        SectionServiceDto dongDaeMoonAndHaegiDto = new SectionServiceDto(lineId, targetUpStationId,
            targetDownStationId, distance);
        SectionServiceDto savedDongDaeMoonAndHaegiDto = sectionService.save(dongDaeMoonAndHaegiDto);
        assertThat(savedDongDaeMoonAndHaegiDto).isNotNull();
        Section changedSection = sectionDao.findByLineIdAndDownStationId(lineId, 3L).get();

        // then
        assertThat(savedDongDaeMoonAndHaegiDto.getLineId()).isEqualTo(lineId);
        assertThat(savedDongDaeMoonAndHaegiDto.getUpStationId()).isEqualTo(targetUpStationId);
        assertThat(savedDongDaeMoonAndHaegiDto.getDownStationId()).isEqualTo(targetDownStationId);
        assertThat(savedDongDaeMoonAndHaegiDto.getDistance()).isEqualTo(distance);

        assertThat(changedSection.getDistance()).isEqualTo(5);
        assertThat(changedSection.getUpStationId()).isEqualTo(9L);
    }

    @Test
    @DisplayName("역 사이에 먼 거리 추가")
    void createSectionBetweenSectionsWithExcessDistance() {
        // given
        long targetUpStationId = 2L;
        long targetDownStationId = 9L;
        int distance = 15;
        String name = "회기역";
        stationDao.save(new Station(name));

        // when
        SectionServiceDto dongDaeMoonAndHaegiDto =
            new SectionServiceDto(lineId, targetUpStationId, targetDownStationId, distance);

        // then
        assertThatThrownBy(() -> sectionService.save(dongDaeMoonAndHaegiDto))
            .isInstanceOf(InvalidDistanceException.class);
    }

    @Test
    @DisplayName("지하철 역이 3개 이상일 때 노선의 중간역 삭제")
    void deleteSection() {
        // given
        DeleteStationDto deleteStationDto = new DeleteStationDto(lineId, stationDongMyoId);

        // when
        sectionService.delete(deleteStationDto);
        Section section = sectionDao.findByLineIdAndUpStationId(lineId, stationSinSeolId)
            .orElseThrow(NotFoundStationException::new);

        // then
        assertThat(section.getUpStationId()).isEqualTo(stationSinSeolId);
        assertThat(section.getDownStationId()).isEqualTo(stationDongDaeMoonId);
        assertThat(section.getDistance()).isEqualTo(20);
    }

    @Test
    @DisplayName("지하철 역이 3개 이상일 때 노선 끝에 존재하는 역(구간) 삭제")
    void deleteSectionAtEnd() {
        // given
        DeleteStationDto deleteStationDto = new DeleteStationDto(lineId, stationDongDaeMoonId);

        // when
        Sections beforeSections = new Sections(sectionDao.findAllByLineId(lineId));
        Deque<Long> beforeStationIds = beforeSections.sortedStationIds();
        sectionService.delete(deleteStationDto);
        Sections afterSections = new Sections(sectionDao.findAllByLineId(lineId));
        Deque<Long> afterStationIds = afterSections.sortedStationIds();

        // then
        assertThat(beforeStationIds.peekFirst()).isEqualTo(stationSinSeolId);
        assertThat(beforeStationIds.peekLast()).isEqualTo(stationDongDaeMoonId);
        assertThat(afterStationIds.peekFirst()).isEqualTo(stationSinSeolId);
        assertThat(afterStationIds.peekLast()).isEqualTo(stationDongMyoId);
    }

    @Test
    @DisplayName("지하철 역이 2개만 있을 때(구간이 1개일 때)의 삭제")
    void deleteSectionWithTwoStations() {
        // given
        DeleteStationDto preparedDeleteStationDto = new DeleteStationDto(lineId, stationDongMyoId);
        DeleteStationDto targetDeleteStationDto = new DeleteStationDto(lineId, stationSinSeolId);

        // when
        sectionService.delete(preparedDeleteStationDto);

        // then
        assertThatThrownBy(() -> sectionService.delete(targetDeleteStationDto))
            .isInstanceOf(IllegalStateException.class);
    }
}
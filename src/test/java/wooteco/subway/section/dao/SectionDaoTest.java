package wooteco.subway.section.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.exception.section.InvalidSectionOnLineException;
import wooteco.subway.line.Line;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.section.Section;
import wooteco.subway.station.Station;
import wooteco.subway.station.dao.StationDao;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql("classpath:initializeTable.sql")
public class SectionDaoTest {

    private final Line line = new Line(1L, "1호선", "파란");
    private final Station stationSinSeol = new Station(1L, "신설동역");
    private final Station stationDongMyo = new Station(2L, "동묘앞역");
    private final Station stationDongDaeMoon = new Station(3L, "동대문역");
    private final int distance = 10;

    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private LineDao lineDao;
    @Autowired
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        lineDao.save(line);
        stationDao.save(stationSinSeol);
        stationDao.save(stationDongMyo);
        stationDao.save(stationDongDaeMoon);
        Section sinSeolAndDongMyo = new Section(line, stationSinSeol, stationDongMyo,
            distance);
        Section dongMyoAndDongDaeMoon = new Section(line, stationDongMyo, stationDongDaeMoon,
            distance);

        sectionDao.save(sinSeolAndDongMyo);
        sectionDao.save(dongMyoAndDongDaeMoon);
    }

    @Test
    @DisplayName("Section 저장 테스트")
    public void save() {
        // given
        Line line = lineDao.show(1L).get();
        Station upStation = stationDao.showStation(1L).get();
        Station downStation = stationDao.showStation(2L).get();
        int distance = 10;

        Section targetSection = new Section(line, upStation, downStation, distance);

        // when
        Section savedSection = sectionDao.save(targetSection);

        // then
        assertThat(savedSection.getLine()).isEqualTo(line);
        assertThat(savedSection.getUpStation()).isEqualTo(upStation);
        assertThat(savedSection.getDownStation()).isEqualTo(downStation);
        assertThat(savedSection.getDistance().getValue()).isEqualTo(distance);
    }

    @Test
    @DisplayName("노선에 있는 Section들 반환 테스트")
    public void findAllByLineId() {
        // given

        // when
        List<Section> sections = sectionDao.findAllByLineId(line.getId());
        Section savedSinSeolAndDongMyo = sections.get(0);
        Section savedDongMyoAndDongDaeMoon = sections.get(1);

        // then
        assertThat(savedSinSeolAndDongMyo.getLine()).isEqualTo(line);
        assertThat(savedSinSeolAndDongMyo.getUpStation()).isEqualTo(stationSinSeol);
        assertThat(savedSinSeolAndDongMyo.getDownStation()).isEqualTo(stationDongMyo);
        assertThat(savedSinSeolAndDongMyo.getDistance().getValue()).isEqualTo(distance);
        assertThat(savedDongMyoAndDongDaeMoon.getLine()).isEqualTo(line);
        assertThat(savedDongMyoAndDongDaeMoon.getUpStation()).isEqualTo(stationDongMyo);
        assertThat(savedDongMyoAndDongDaeMoon.getDownStation()).isEqualTo(stationDongDaeMoon);
        assertThat(savedDongMyoAndDongDaeMoon.getDistance().getValue()).isEqualTo(distance);
    }

    @Test
    @DisplayName("노선에 있는 상행역 조회 테스트")
    public void findByLineIdAndUpStationId() {
        // given

        // when
        Section section = sectionDao.findByLineIdAndUpStationId(line.getId(), stationSinSeol.getId())
            .orElseThrow(InvalidSectionOnLineException::new);

        // then
        assertThat(section.getLine()).isEqualTo(line);
        assertThat(section.getUpStation()).isEqualTo(stationSinSeol);
        assertThat(section.getDownStation()).isEqualTo(stationDongMyo);
        assertThat(section.getDistance().getValue()).isEqualTo(distance);
    }

    @Test
    @DisplayName("노선에 있는 하행역 조회 테스트")
    public void findByLineIdAndDownStationId() {
        // given

        // when
        Section section = sectionDao.findByLineIdAndDownStationId(line.getId(), stationDongMyo.getId())
            .orElseThrow(InvalidSectionOnLineException::new);

        // then
        assertThat(section.getLine()).isEqualTo(line);
        assertThat(section.getUpStation()).isEqualTo(stationSinSeol);
        assertThat(section.getDownStation()).isEqualTo(stationDongMyo);
        assertThat(section.getDistance().getValue()).isEqualTo(distance);
    }

    @Test
    @DisplayName("노선에 있는 상/하행역 조회 예외처리")
    public void findByLineIdAndStationId() {
        // given

        // when

        // then
        assertThat(sectionDao.findByLineIdAndUpStationId(line.getId(), 9L)).isEmpty();
        assertThat(sectionDao.findByLineIdAndUpStationId(2L, stationDongMyo.getId())).isEmpty();
        assertThat(sectionDao.findByLineIdAndDownStationId(2L, stationDongMyo.getId())).isEmpty();
        assertThat(sectionDao.findByLineIdAndUpStationId(line.getId(), stationDongDaeMoon.getId())).isEmpty();
    }

    @Test
    @DisplayName("구간 중 상행역 제거")
    public void deleteByLineIdAndUpStationId() {
        // given
        Long targetRemoveStationId = 1L;

        // when
        List<Section> beforeAllByLineId = sectionDao.findAllByLineId(line.getId());
        int result = sectionDao.deleteByLineIdAndUpStationId(line.getId(), targetRemoveStationId);
        List<Section> afterAllByLindId = sectionDao.findAllByLineId(line.getId());

        //then
        assertThat(result).isEqualTo(1);
        assertThat(beforeAllByLineId).hasSize(2);
        assertThat(afterAllByLindId).hasSize(1);
    }

    @Test
    @DisplayName("구간 중 하행역 제거")
    public void deleteByLineIdAndDownStationId() {
        Long targetRemoveStationId = 3L;

        // when
        List<Section> beforeAllByLineId = sectionDao.findAllByLineId(line.getId());
        int result = sectionDao.deleteByLineIdAndDownStationId(line.getId(), targetRemoveStationId);
        List<Section> afterAllByLindId = sectionDao.findAllByLineId(line.getId());

        //then
        assertThat(result).isEqualTo(1);
        assertThat(beforeAllByLineId).hasSize(2);
        assertThat(afterAllByLindId).hasSize(1);
    }

    @Test
    @DisplayName("구간 제거")
    public void delete() {
        // given
        Long targetRemoveSectionId = 1L;
        Section section = new Section(1L, line, stationSinSeol, stationDongMyo, 10);
        section = sectionDao.save(section);

        // when
        List<Section> beforeAllByLineId = sectionDao.findAllByLineId(line.getId());
        int result = sectionDao.delete(section);
        List<Section> afterAllByLindId = sectionDao.findAllByLineId(line.getId());

        // then
        assertThat(result).isEqualTo(1);
        assertThat(beforeAllByLineId).hasSize(3);
        assertThat(afterAllByLindId).hasSize(2);
    }
}

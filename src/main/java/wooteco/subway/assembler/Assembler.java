package wooteco.subway.assembler;

import wooteco.subway.line.LineService;
import wooteco.subway.line.dao.LineDaoMemory;
import wooteco.subway.station.StationService;
import wooteco.subway.station.dao.StationDaoCache;

public class Assembler {

    private final StationService stationService;
    private final LineService lineService;

    public Assembler() {
        this.stationService = new StationService(new StationDaoCache());
        this.lineService = new LineService(new LineDaoMemory());
    }

    public StationService getStationService() {
        return stationService;
    }

    public LineService getLineService() {
        return lineService;
    }
}

package wooteco.subway.exception;

public class InvalidSectionOnLineException extends SubwayException {

    public InvalidSectionOnLineException() {
        super("해당 노선에 입력된 구간을 추가할 수 없습니다.");
    }
}

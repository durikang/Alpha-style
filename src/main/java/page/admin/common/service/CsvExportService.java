package page.admin.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
@Slf4j
public class CsvExportService {

    /**
     * CSV 파일을 생성하여 응답으로 전송합니다.
     *
     * @param response       HttpServletResponse 객체
     * @param fileName       다운로드할 파일명
     * @param header         CSV 헤더
     * @param dataLines      데이터 라인 리스트
     * @throws IOException IO 예외 발생 시
     */
    public void exportToCsv(HttpServletResponse response, String fileName, String header, List<String> dataLines) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/csv;charset=UTF-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

        // BOM 추가 (Excel 호환성 개선)
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8);
        PrintWriter writer = new PrintWriter(outputStreamWriter);
        writer.write('\uFEFF'); // UTF-8 BOM

        writer.println(header);
        for (String line : dataLines) {
            writer.println(line);
        }

        writer.flush();
        writer.close();
        log.info("CSV 파일 다운로드 완료 - 파일명: {}", fileName);
    }

    /**
     * CSV 필드 이스케이프 처리
     *
     * @param field CSV 필드 값
     * @return 이스케이프 처리된 필드 값
     */
    public String escapeCsv(String field) {
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            field = field.replace("\"", "\"\"");
            return "\"" + field + "\"";
        }
        return field;
    }

    /**
     * 날짜를 문자열로 포맷팅하는 SimpleDateFormat을 반환합니다.
     *
     * @return SimpleDateFormat 객체
     */
    public SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }
}

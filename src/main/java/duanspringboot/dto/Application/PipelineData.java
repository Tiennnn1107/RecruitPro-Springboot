package duanspringboot.dto.Application;

import lombok.Data;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class PipelineData {
    private List<ApplicationResponse> pendingApps;
    private List<ApplicationResponse> reviewingApps;
    private List<ApplicationResponse> interviewingApps;
    private List<ApplicationResponse> finalApps;
    private Map<String, Integer> counts;
}

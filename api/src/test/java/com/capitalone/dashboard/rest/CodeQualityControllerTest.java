package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.config.TestConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CodeQualityMetricStatus;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.model.DataResponse;
import com.capitalone.dashboard.request.CodeQualityRequest;
import com.capitalone.dashboard.service.CodeQualityService;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class, WebMVCConfig.class })
@WebAppConfiguration
public class CodeQualityControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;
	@Autowired
	private CodeQualityService codeQualityService;

	@Before
	public void before() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void staticQualities() throws Exception {
		CodeQuality quality = makeCodeQualityStatic();
		Iterable<CodeQuality> qualities = Arrays.asList(quality);
		DataResponse<Iterable<CodeQuality>> response = new DataResponse<>(
				qualities, 1);
		CodeQualityMetric metric = makeMetric();

		when(codeQualityService.search(Mockito.any(CodeQualityRequest.class)))
				.thenReturn(response);
		mockMvc.perform(
				get("/quality/static-analysis?componentId=" + ObjectId.get() + "&max=1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$result", hasSize(1)))
				.andExpect(
						jsonPath("$result[0].id",
								is(quality.getId().toString())))
				.andExpect(
						jsonPath("$result[0].collectorItemId", is(quality
								.getCollectorItemId().toString())))
				.andExpect(
						jsonPath("$result[0].timestamp",
								is(intVal(quality.getTimestamp()))))
				.andExpect(jsonPath("$result[0].name", is(quality.getName().toString())))
				.andExpect(jsonPath("$result[0].url", is(quality.getUrl())))
				.andExpect(jsonPath("$result[0].type", is(quality.getType().toString())))
				.andExpect(
						jsonPath("$result[0].version", is(quality.getVersion())))
				.andExpect(
						jsonPath("$result[0].metrics[0].name",
								is(metric.getName().toString())))
				.andExpect(
						jsonPath("$result[0].metrics[0].formattedValue",
								is(metric.getFormattedValue())))
				.andExpect(
						jsonPath("$result[0].metrics[0].status",
								is(metric.getStatus().toString())));
	}

	@Test
	public void securityQualities() throws Exception {
		CodeQuality quality = makeSecurityAnalysis();
		Iterable<CodeQuality> qualities = Arrays.asList(quality);
		DataResponse<Iterable<CodeQuality>> response = new DataResponse<>(
				qualities, 1);
		CodeQualityMetric metric = makeMetric();
		when(codeQualityService.search(Mockito.any(CodeQualityRequest.class)))
				.thenReturn(response);
		mockMvc.perform(
				get("/quality/security-analysis?componentId=" + ObjectId.get() + "&max=1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$result", hasSize(1)))
				.andExpect(
						jsonPath("$result[0].id",
								is(quality.getId().toString())))
				.andExpect(
						jsonPath("$result[0].collectorItemId", is(quality
								.getCollectorItemId().toString())))
				.andExpect(
						jsonPath("$result[0].timestamp",
								is(intVal(quality.getTimestamp()))))
				.andExpect(jsonPath("$result[0].name", is(quality.getName())))
				.andExpect(jsonPath("$result[0].url", is(quality.getUrl())))
				.andExpect(jsonPath("$result[0].type", is(quality.getType().toString())))
				.andExpect(
						jsonPath("$result[0].version", is(quality.getVersion())))
				.andExpect(
						jsonPath("$result[0].metrics[0].name",
								is(metric.getName())))
				.andExpect(
						jsonPath("$result[0].metrics[0].formattedValue",
								is(metric.getFormattedValue())))
				.andExpect(
						jsonPath("$result[0].metrics[0].status",
								is(metric.getStatus().toString())));
	}

	@Test
	public void builds_noComponentId_badRequest() throws Exception {
		mockMvc.perform(get("/quality")).andExpect(status().isBadRequest());
	}

	private CodeQuality makeCodeQualityStatic() {
		CodeQuality quality = new CodeQuality();
		quality.setId(ObjectId.get());
		quality.setCollectorItemId(ObjectId.get());
		quality.setTimestamp(1);
		quality.setName("MyTest");
		quality.setType(CodeQualityType.StaticAnalysis);
		quality.setUrl("http://mycompany.sonar.com/MyTest");
		quality.setVersion("1.0.0.1");
		quality.getMetrics().add(makeMetric());
		return quality;
	}

	private CodeQuality makeSecurityAnalysis() {
		CodeQuality quality = new CodeQuality();
		quality.setId(ObjectId.get());
		quality.setCollectorItemId(ObjectId.get());
		quality.setTimestamp(1);
		quality.setName("MyFortify");
		quality.setType(CodeQualityType.SecurityAnalysis);
		quality.setUrl("http://mycompany.fortify.ssc.com/MyFortify");
		quality.setVersion("dev");
		quality.getMetrics().add(makeMetric());
		return quality;
	}

	private CodeQualityMetric makeMetric() {
		CodeQualityMetric metric = new CodeQualityMetric("critical");
		metric.setFormattedValue("10");
		metric.setStatus(CodeQualityMetricStatus.Ok);
		metric.setStatusMessage("Ok");
		metric.setValue(new Integer(0));
		return metric;
	}

	private int intVal(long value) {
		return Long.valueOf(value).intValue();
	}

}

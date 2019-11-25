package finra.common.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import finra.create.fileanduser.CreateFileAndUser;
import finra.dto.GrantReadAccessDto;

public class HelperWebScript  {

	public static byte[] toJson(Object object) {
	    final ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.setSerializationInclusion(org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL);
	    try {
	        mapper.writeValue(out, object);
	    } catch (IOException e) {
	        if (CreateFileAndUser.log.isDebugEnabled()) {
	            CreateFileAndUser.log.debug(e.getMessage(), e);
	        }
	    }
	    return out.toByteArray();
	}

	public static GrantReadAccessDto unMarshall(String jsonString, Class<GrantReadAccessDto> targetClass) throws IOException {
	    ObjectMapper mapper = incomingMapperClientJsonToDto();
	    return mapper.readValue(jsonString, targetClass);
	}

	public static ObjectMapper incomingMapperClientJsonToDto() {
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.setSerializationInclusion(org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL);
	    return mapper;
	}



}

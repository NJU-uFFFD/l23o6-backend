package org.fffd.l23o6.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response<T> {
	private int status;
	private String msg;
	private T data;
}
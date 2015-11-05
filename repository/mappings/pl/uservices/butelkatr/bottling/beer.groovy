io.codearte.accurest.dsl.GroovyDsl.make {
	request {
		method "POST"
		url "/beer"
		body( '{ "quantity":1000 }' )
		headers { header("Content-Type", "application/butelkator.v1+json") }
	}
	response {
		status 204
	}
}
io.codearte.accurest.dsl.GroovyDsl.make {
    request {
        method 'POST'
        url '/bottle'
        headers {
            header 'Content-Type': 'application/vnd.pl.uservices.butelkatr.v1+json'
        }
        body ('''{
            "wort": 1000
        }''')
    }
    response {
        status 200
    }
}
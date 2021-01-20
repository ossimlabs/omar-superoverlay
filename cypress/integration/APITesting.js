let json = require('../../testParameters.json');
let tests = Object.keys(json.tests);
let innerJson, method, endpoint, query, good, parameters, request, keys;

describe('Automated tests for the omar-superoverlay API endpoints', () => {
    tests.forEach((test) => {
            it(`Should test ${test}`, () => {
                innerJson = json.tests[test]
                method = innerJson["method"];
                endpoint = innerJson["endpoint"];
                query = innerJson["in"] === "query";
                good = innerJson["expected"] === "good";
                parameters = Object.keys(innerJson["parameters"]);
                if(query && good) {
                    request = "?"
                    parameters.forEach((parameter) => {
                        request = request + parameter + "=" + innerJson.parameters[parameter] + "&";
                    })
                    request = request.substring(0, request.length - 1);
                    cy.request({method: method, url: endpoint + request, failOnStatusCode: false})
                        .then((response) => {
                            expect(response.status).to.eq(200)
                        })
                }
                else if(query) {
                    request = "?"
                    parameters.forEach((parameter) => {
                        request = request + parameter + "=" + innerJson.parameters[parameter] + "&";
                    })
                    request = request.substring(0, request.length - 1);
                    cy.request({method: method, url: endpoint + request, failOnStatusCode: false})
                        .then((response) => {
                            expect(response.status).to.not.eq(200)
                    })
                }
                else if(good) {
                    cy.request({method: method, url: endpoint, body: innerJson.parameters["body"], failOnStatusCode: false})
                        .then((response) => {
                            expect(response.status).to.eq(200)
                    })
                }
            })
            it(`Should test the header of ${test}`, () => {
                innerJson = json.tests[test]
                method = innerJson["method"];
                endpoint = innerJson["endpoint"];
                query = innerJson["in"] === "query";
                good = innerJson["expected"] === "good";
                parameters = Object.keys(innerJson["parameters"]);
                if(query) {
                    cy.request({method: method, url: endpoint + request, failOnStatusCode: false})
                        .then((response) => {
                            expect(response).to.have.property("headers")
                        })
                }
                else {
                    cy.request({method: method, url: endpoint, body: innerJson.parameters["body"], failOnStatusCode: false})
                        .then((response) => {
                            expect(response).to.have.property("headers")
                        })
                }
            })
    })
})
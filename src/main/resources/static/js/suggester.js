const query = document.getElementById("query_holder");
const Http = new XMLHttpRequest();
const url = 'https://acl0ud.herokuapp.com/suggestions';

query.onchange = function() {
    console.log(query)
    // Http.open('GET', url);
    // Http.send();
}
function copyToClipboard() {
    const link = document.location.host.toString() + document.getElementById("link").value.toString();
    navigator.clipboard.writeText(link).then(() => alert("Link copied to clipboard: " + link));
}
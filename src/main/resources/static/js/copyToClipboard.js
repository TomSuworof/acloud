function copyToClipboard() {
    const copyLink = document.getElementById("link");
    const domain = document.location.host;
    navigator.clipboard.writeText(domain.toString() + copyLink.value).then(() => alert("Link copied to clipboard"));
}
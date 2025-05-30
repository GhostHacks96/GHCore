async function loadModules() {
  const response = await fetch('modules.json');
  const modules = await response.json();
  const resultsDiv = document.getElementById('results');
  const searchInput = document.getElementById('search');

  function display(mods) {
    resultsDiv.innerHTML = '';
    mods.forEach(mod => {
      const div = document.createElement('div');
      div.className = 'plugin';
      div.innerHTML = `
        <h3>${mod.name} (v${mod.version})</h3>
        <p>${mod.description}</p>
        <p><strong>Tags:</strong> ${mod.tags.join(', ')}</p>
        <a href="${mod.url}" target="_blank">Download</a>
      `;
      resultsDiv.appendChild(div);
    });
  }

  searchInput.addEventListener('input', () => {
    const query = searchInput.value.toLowerCase();
    const filtered = modules.filter(m =>
      m.name.toLowerCase().includes(query) ||
      m.description.toLowerCase().includes(query) ||
      m.tags.some(tag => tag.toLowerCase().includes(query))
    );
    display(filtered);
  });

  display(modules);
}

loadModules();

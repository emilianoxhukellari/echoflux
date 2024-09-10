import '@vaadin/icon/vaadin-iconset.js';

const template = document.createElement('template');

template.innerHTML = `
<vaadin-iconset name="customized-iconset">
  <svg><defs>
    <g id="customized-iconset:filter-slash"><svg xmlns="http://www.w3.org/2000/svg" viewBox="0 -960 960 960"><path d="m592-481-57-57 143-182H353l-80-80h487q25 0 36 22t-4 42L592-481ZM791-56 560-287v87q0 17-11.5 28.5T520-160h-80q-17 0-28.5-11.5T400-200v-247L56-791l56-57 736 736-57 56ZM535-538Z"/></svg></g>
  </defs></svg>
</vaadin-iconset>
`;

document.head.appendChild(template.content);

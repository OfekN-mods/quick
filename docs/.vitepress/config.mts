import { defineConfig } from 'vitepress'

export default defineConfig({
  title: "Quick",
  description: "API documentation for the Quick Minecraft mod",
  base: '/quick/',

  themeConfig: {
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Guide', link: '/guide/getting-started' },
      { text: 'API Reference', link: '/reference/' },
    ],

    sidebar: [
      {
        text: 'Guide',
        items: [
          { text: 'Introduction', link: '/guide/getting-started' },
          { text: 'Item Actions', link: '/guide/item-actions' },
          { text: 'Custom Actions', link: '/guide/custom-actions' },
        ]
      },
      {
        text: 'API Reference',
        items: [
          { text: 'Slot System', link: '/reference/slots' },
          { text: 'Custom Wheels', link: '/reference/custom-wheels' },
        ]
      }
    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/OfekN-mods/quick' },
      { icon: 'discord', link: 'https://discord.gg/TXmFRWcpQ2' },
      { icon: 'curseforge', link: 'https://www.curseforge.com/minecraft/mc-mods/quick' },
    ]
  }
})

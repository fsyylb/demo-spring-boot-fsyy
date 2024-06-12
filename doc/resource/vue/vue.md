# vue cli

Vue CLI 现已处于维护模式!
现在官方推荐使用 create-vue 来创建基于 Vite 的新项目。另外请参考 Vue 3 工具链指南 以了解最新的工具推荐。

https://cli.vuejs.org/zh/guide/
起步
安装：
npm install -g @vue/cli
OR
yarn global add @vue/cli

创建一个项目：
vue create my-project
OR
vue ui

# element-plus
element-ui不支持vue3，所以有了element-plus

https://element-plus.org/zh-CN/guide/quickstart.html

完整引入#
如果你对打包后的文件大小不是很在乎，那么使用完整导入会更方便。

// main.ts
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'

const app = createApp(App)

app.use(ElementPlus)
app.mount('#app')

# vue代理跨域使用axios实现百度搜索功能
https://blog.csdn.net/m0_72189003/article/details/127443543

# Axios 跨域问题 No ‘Access-Control-Allow-Origin‘ header is present on the requested resource.
https://blog.csdn.net/qq_41992943/article/details/118544389

# axios
https://www.axios-http.cn/

# NProgress使用教程
https://www.cnblogs.com/d-lir/p/14018641.html

https://blog.csdn.net/CEZLZ/article/details/108198402

# vue官网
https://cn.vuejs.org/guide/quick-start.html
https://vue3js.cn/


# vuex官网
https://vuex.vuejs.org/zh/

# vue router
https://router.vuejs.org/zh/guide/essentials/dynamic-matching.html

# 防抖
Vue.directive('debounce', { //防抖函数指令
      inserted: function(el, binding) {
        let timer;
        el.addEventListener("click", () => {
          if (timer) {
            clearTimeout(timer);
          }
          timer = setTimeout(() => {
          //关键点:vue 的自定义指令传递的参数binding 如果是一个函数,则通过      binding.value()来执行,通过上述示例,还可以传递比如事件, 绑定对象之类的
            binding.value();
          }, 1000);
        });
      }
   })

# sessionStorage
export function setSessionStorage(name, value){
    window.sessionStorage.setItem(name, JSON.stringify(value))
}

# Moment.js
https://momentjs.cn/guides/


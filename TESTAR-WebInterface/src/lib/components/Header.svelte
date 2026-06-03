<header class="header">
 <a href="/" class="menulinks">  <img alt="The project logo" src={logo}> </a> 
 <button class="btn" type="button" on:click={help}>Help</button>
</header>

<script>
	import logo from '$lib/assets/testar.png';

  function help(){
        window.open('/Hands_on_TESTAR_Training_Manual_2025_July.pdf', '_blank');
    }
</script>

<style>
  .header {
    display: flex;
    align-items: center;
    padding: 10px; /* adds space around logo */
    background-color: #ECECF4;
    flex-direction: row;
    justify-content: space-between;
  }
</style>